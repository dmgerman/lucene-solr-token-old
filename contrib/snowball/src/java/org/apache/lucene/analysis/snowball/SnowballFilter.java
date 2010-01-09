begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.snowball
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|snowball
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|tr
operator|.
name|TurkishLowerCaseFilter
import|;
end_import
begin_comment
comment|// javadoc @link
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
name|LowerCaseFilter
import|;
end_import
begin_comment
comment|// javadoc @link
end_comment
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|SnowballProgram
import|;
end_import
begin_comment
comment|/**  * A filter that stems words using a Snowball-generated stemmer.  *  * Available stemmers are listed in {@link org.tartarus.snowball.ext}.  *<p><b>NOTE</b>: SnowballFilter expects lowercased text.  *<ul>  *<li>For the Turkish language, see {@link TurkishLowerCaseFilter}.  *<li>For other languages, see {@link LowerCaseFilter}.  *</ul>  *</p>  */
end_comment
begin_class
DECL|class|SnowballFilter
specifier|public
specifier|final
class|class
name|SnowballFilter
extends|extends
name|TokenFilter
block|{
DECL|field|stemmer
specifier|private
name|SnowballProgram
name|stemmer
decl_stmt|;
DECL|field|termAtt
specifier|private
name|TermAttribute
name|termAtt
decl_stmt|;
DECL|method|SnowballFilter
specifier|public
name|SnowballFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|SnowballProgram
name|stemmer
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|stemmer
operator|=
name|stemmer
expr_stmt|;
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct the named stemming filter.    *    * Available stemmers are listed in {@link org.tartarus.snowball.ext}.    * The name of a stemmer is the part of the class name before "Stemmer",    * e.g., the stemmer in {@link org.tartarus.snowball.ext.EnglishStemmer} is named "English".    *    * @param in the input tokens to stem    * @param name the name of a stemmer    */
DECL|method|SnowballFilter
specifier|public
name|SnowballFilter
parameter_list|(
name|TokenStream
name|in
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|stemClass
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.tartarus.snowball.ext."
operator|+
name|name
operator|+
literal|"Stemmer"
argument_list|)
decl_stmt|;
name|stemmer
operator|=
operator|(
name|SnowballProgram
operator|)
name|stemClass
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|termAtt
operator|=
name|addAttribute
argument_list|(
name|TermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the next input Token, after being stemmed */
annotation|@
name|Override
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
name|char
name|termBuffer
index|[]
init|=
name|termAtt
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|termAtt
operator|.
name|termLength
argument_list|()
decl_stmt|;
name|stemmer
operator|.
name|setCurrent
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|stemmer
operator|.
name|stem
argument_list|()
expr_stmt|;
specifier|final
name|StringBuilder
name|finalTerm
init|=
name|stemmer
operator|.
name|getCurrentBuffer
argument_list|()
decl_stmt|;
specifier|final
name|int
name|newLength
init|=
name|finalTerm
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|newLength
operator|>
name|termBuffer
operator|.
name|length
condition|)
name|termBuffer
operator|=
name|termAtt
operator|.
name|resizeTermBuffer
argument_list|(
name|newLength
argument_list|)
expr_stmt|;
name|finalTerm
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|newLength
argument_list|,
name|termBuffer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setTermLength
argument_list|(
name|newLength
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
block|}
end_class
end_unit
