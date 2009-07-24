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
begin_comment
comment|/** Transforms the token stream as per the Porter stemming algorithm.     Note: the input to the stemming filter must already be in lower case,     so you will need to use LowerCaseFilter or LowerCaseTokenizer farther     down the Tokenizer chain in order for this to work properly!<P>     To use this filter with other analyzers, you'll want to write an     Analyzer class that sets up the TokenStream chain as you want it.     To use this with LowerCaseTokenizer, for example, you'd write an     analyzer like this:<P><PRE>     class MyAnalyzer extends Analyzer {       public final TokenStream tokenStream(String fieldName, Reader reader) {         return new PorterStemFilter(new LowerCaseTokenizer(reader));       }     }</PRE> */
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
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|method|PorterStemFilter
specifier|public
name|PorterStemFilter
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
name|PorterStemmer
argument_list|()
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
operator|!
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|stemmer
operator|.
name|stem
argument_list|(
name|termAtt
operator|.
name|termBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|termLength
argument_list|()
argument_list|)
condition|)
name|termAtt
operator|.
name|setTermBuffer
argument_list|(
name|stemmer
operator|.
name|getResultBuffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|stemmer
operator|.
name|getResultLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class
end_unit
