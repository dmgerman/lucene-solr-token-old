begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
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
name|Hashtable
import|;
end_import
begin_comment
comment|/** Transforms the token stream as per the Porter stemming algorithm.      Note: the input to the stemming filter must already be in lower case,      so you will need to use LowerCaseFilter or LowerCaseTokenizer farther     down the Tokenizer chain in order for this to work properly!        To use this filter with other analyzers, you'll want to write an      Analyzer class that sets up the TokenStream chain as you want it.       To use this with LowerCaseTokenizer, for example, you'd write an     analyzer like this:      class MyAnalyzer extends Analyzer {       public final TokenStream tokenStream(String fieldName, Reader reader) {         return new PorterStemFilter(new LowerCaseTokenizer(reader));       }     }   */
end_comment
begin_class
DECL|class|PorterStemFilter
specifier|public
specifier|final
class|class
name|PorterStemFilter
extends|extends
name|TokenFilter
block|{
DECL|field|stemmer
specifier|private
name|PorterStemmer
name|stemmer
decl_stmt|;
DECL|method|PorterStemFilter
specifier|public
name|PorterStemFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|)
block|{
name|stemmer
operator|=
operator|new
name|PorterStemmer
argument_list|()
expr_stmt|;
name|input
operator|=
name|in
expr_stmt|;
block|}
comment|/** Returns the next input Token, after being stemmed */
DECL|method|next
specifier|public
specifier|final
name|Token
name|next
parameter_list|()
throws|throws
name|IOException
block|{
name|Token
name|token
init|=
name|input
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
return|return
literal|null
return|;
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|token
operator|.
name|termText
condition|)
comment|// Yes, I mean object reference comparison here
name|token
operator|.
name|termText
operator|=
name|s
expr_stmt|;
return|return
name|token
return|;
block|}
block|}
block|}
end_class
end_unit
