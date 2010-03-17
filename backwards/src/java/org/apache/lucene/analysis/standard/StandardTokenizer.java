begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.standard
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
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
name|io
operator|.
name|Reader
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
name|Tokenizer
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
name|PositionIncrementAttribute
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
name|tokenattributes
operator|.
name|TypeAttribute
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
name|AttributeSource
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
comment|/** A grammar-based tokenizer constructed with JFlex  *  *<p> This should be a good tokenizer for most European-language documents:  *  *<ul>  *<li>Splits words at punctuation characters, removing punctuation. However, a   *     dot that's not followed by whitespace is considered part of a token.  *<li>Splits words at hyphens, unless there's a number in the token, in which case  *     the whole token is interpreted as a product number and is not split.  *<li>Recognizes email addresses and internet hostnames as one token.  *</ul>  *  *<p>Many applications have specific tokenizer needs.  If this tokenizer does  * not suit your application, please consider copying this source code  * directory to your project and maintaining your own grammar-based tokenizer.  *  *<a name="version"/>  *<p>You must specify the required {@link Version}  * compatibility when creating StandardAnalyzer:  *<ul>  *<li> As of 2.4, Tokens incorrectly identified as acronyms  *        are corrected (see<a href="https://issues.apache.org/jira/browse/LUCENE-1068">LUCENE-1608</a>  *</ul>  */
end_comment
begin_class
DECL|class|StandardTokenizer
specifier|public
specifier|final
class|class
name|StandardTokenizer
extends|extends
name|Tokenizer
block|{
comment|/** A private instance of the JFlex-constructed scanner */
DECL|field|scanner
specifier|private
specifier|final
name|StandardTokenizerImpl
name|scanner
decl_stmt|;
DECL|field|ALPHANUM
specifier|public
specifier|static
specifier|final
name|int
name|ALPHANUM
init|=
literal|0
decl_stmt|;
DECL|field|APOSTROPHE
specifier|public
specifier|static
specifier|final
name|int
name|APOSTROPHE
init|=
literal|1
decl_stmt|;
DECL|field|ACRONYM
specifier|public
specifier|static
specifier|final
name|int
name|ACRONYM
init|=
literal|2
decl_stmt|;
DECL|field|COMPANY
specifier|public
specifier|static
specifier|final
name|int
name|COMPANY
init|=
literal|3
decl_stmt|;
DECL|field|EMAIL
specifier|public
specifier|static
specifier|final
name|int
name|EMAIL
init|=
literal|4
decl_stmt|;
DECL|field|HOST
specifier|public
specifier|static
specifier|final
name|int
name|HOST
init|=
literal|5
decl_stmt|;
DECL|field|NUM
specifier|public
specifier|static
specifier|final
name|int
name|NUM
init|=
literal|6
decl_stmt|;
DECL|field|CJ
specifier|public
specifier|static
specifier|final
name|int
name|CJ
init|=
literal|7
decl_stmt|;
comment|/**    * @deprecated this solves a bug where HOSTs that end with '.' are identified    *             as ACRONYMs.    */
DECL|field|ACRONYM_DEP
specifier|public
specifier|static
specifier|final
name|int
name|ACRONYM_DEP
init|=
literal|8
decl_stmt|;
comment|/** String token types that correspond to token type int constants */
DECL|field|TOKEN_TYPES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|TOKEN_TYPES
init|=
operator|new
name|String
index|[]
block|{
literal|"<ALPHANUM>"
block|,
literal|"<APOSTROPHE>"
block|,
literal|"<ACRONYM>"
block|,
literal|"<COMPANY>"
block|,
literal|"<EMAIL>"
block|,
literal|"<HOST>"
block|,
literal|"<NUM>"
block|,
literal|"<CJ>"
block|,
literal|"<ACRONYM_DEP>"
block|}
decl_stmt|;
DECL|field|replaceInvalidAcronym
specifier|private
name|boolean
name|replaceInvalidAcronym
decl_stmt|;
DECL|field|maxTokenLength
specifier|private
name|int
name|maxTokenLength
init|=
name|StandardAnalyzer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
comment|/** Set the max allowed token length.  Any token longer    *  than this is skipped. */
DECL|method|setMaxTokenLength
specifier|public
name|void
name|setMaxTokenLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|maxTokenLength
operator|=
name|length
expr_stmt|;
block|}
comment|/** @see #setMaxTokenLength */
DECL|method|getMaxTokenLength
specifier|public
name|int
name|getMaxTokenLength
parameter_list|()
block|{
return|return
name|maxTokenLength
return|;
block|}
comment|/**    * Creates a new instance of the {@link org.apache.lucene.analysis.standard.StandardTokenizer}.  Attaches    * the<code>input</code> to the newly created JFlex scanner.    *    * @param input The input reader    *    * See http://issues.apache.org/jira/browse/LUCENE-1068    */
DECL|method|StandardTokenizer
specifier|public
name|StandardTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|scanner
operator|=
operator|new
name|StandardTokenizerImpl
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|input
argument_list|,
name|matchVersion
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new StandardTokenizer with a given {@link AttributeSource}.     */
DECL|method|StandardTokenizer
specifier|public
name|StandardTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|scanner
operator|=
operator|new
name|StandardTokenizerImpl
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|input
argument_list|,
name|matchVersion
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a new StandardTokenizer with a given {@link org.apache.lucene.util.AttributeSource.AttributeFactory}     */
DECL|method|StandardTokenizer
specifier|public
name|StandardTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|this
operator|.
name|scanner
operator|=
operator|new
name|StandardTokenizerImpl
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|init
argument_list|(
name|input
argument_list|,
name|matchVersion
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|Reader
name|input
parameter_list|,
name|Version
name|matchVersion
parameter_list|)
block|{
if|if
condition|(
name|matchVersion
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|LUCENE_24
argument_list|)
condition|)
block|{
name|replaceInvalidAcronym
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|replaceInvalidAcronym
operator|=
literal|false
expr_stmt|;
block|}
name|this
operator|.
name|input
operator|=
name|input
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
name|offsetAtt
operator|=
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncrAtt
operator|=
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|typeAtt
operator|=
name|addAttribute
argument_list|(
name|TypeAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// this tokenizer generates three attributes:
comment|// offset, positionIncrement and type
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
DECL|field|posIncrAtt
specifier|private
name|PositionIncrementAttribute
name|posIncrAtt
decl_stmt|;
DECL|field|typeAtt
specifier|private
name|TypeAttribute
name|typeAtt
decl_stmt|;
comment|/*    * (non-Javadoc)    *    * @see org.apache.lucene.analysis.TokenStream#next()    */
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
name|clearAttributes
argument_list|()
expr_stmt|;
name|int
name|posIncr
init|=
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|tokenType
init|=
name|scanner
operator|.
name|getNextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenType
operator|==
name|StandardTokenizerImpl
operator|.
name|YYEOF
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|scanner
operator|.
name|yylength
argument_list|()
operator|<=
name|maxTokenLength
condition|)
block|{
name|posIncrAtt
operator|.
name|setPositionIncrement
argument_list|(
name|posIncr
argument_list|)
expr_stmt|;
name|scanner
operator|.
name|getText
argument_list|(
name|termAtt
argument_list|)
expr_stmt|;
specifier|final
name|int
name|start
init|=
name|scanner
operator|.
name|yychar
argument_list|()
decl_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|correctOffset
argument_list|(
name|start
argument_list|)
argument_list|,
name|correctOffset
argument_list|(
name|start
operator|+
name|termAtt
operator|.
name|termLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// This 'if' should be removed in the next release. For now, it converts
comment|// invalid acronyms to HOST. When removed, only the 'else' part should
comment|// remain.
if|if
condition|(
name|tokenType
operator|==
name|StandardTokenizerImpl
operator|.
name|ACRONYM_DEP
condition|)
block|{
if|if
condition|(
name|replaceInvalidAcronym
condition|)
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
name|StandardTokenizerImpl
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizerImpl
operator|.
name|HOST
index|]
argument_list|)
expr_stmt|;
name|termAtt
operator|.
name|setTermLength
argument_list|(
name|termAtt
operator|.
name|termLength
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// remove extra '.'
block|}
else|else
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
name|StandardTokenizerImpl
operator|.
name|TOKEN_TYPES
index|[
name|StandardTokenizerImpl
operator|.
name|ACRONYM
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|typeAtt
operator|.
name|setType
argument_list|(
name|StandardTokenizerImpl
operator|.
name|TOKEN_TYPES
index|[
name|tokenType
index|]
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
comment|// When we skip a too-long term, we still increment the
comment|// position increment
name|posIncr
operator|++
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|end
specifier|public
specifier|final
name|void
name|end
parameter_list|()
block|{
comment|// set final offset
name|int
name|finalOffset
init|=
name|correctOffset
argument_list|(
name|scanner
operator|.
name|yychar
argument_list|()
operator|+
name|scanner
operator|.
name|yylength
argument_list|()
argument_list|)
decl_stmt|;
name|offsetAtt
operator|.
name|setOffset
argument_list|(
name|finalOffset
argument_list|,
name|finalOffset
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *    * @see org.apache.lucene.analysis.TokenStream#reset()    */
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
name|scanner
operator|.
name|yyreset
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|Reader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|reset
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**    * Prior to https://issues.apache.org/jira/browse/LUCENE-1068, StandardTokenizer mischaracterized as acronyms tokens like www.abc.com    * when they should have been labeled as hosts instead.    * @return true if StandardTokenizer now returns these tokens as Hosts, otherwise false    *    * @deprecated Remove in 3.X and make true the only valid value    */
DECL|method|isReplaceInvalidAcronym
specifier|public
name|boolean
name|isReplaceInvalidAcronym
parameter_list|()
block|{
return|return
name|replaceInvalidAcronym
return|;
block|}
comment|/**    *    * @param replaceInvalidAcronym Set to true to replace mischaracterized acronyms as HOST.    * @deprecated Remove in 3.X and make true the only valid value    *    * See https://issues.apache.org/jira/browse/LUCENE-1068    */
DECL|method|setReplaceInvalidAcronym
specifier|public
name|void
name|setReplaceInvalidAcronym
parameter_list|(
name|boolean
name|replaceInvalidAcronym
parameter_list|)
block|{
name|this
operator|.
name|replaceInvalidAcronym
operator|=
name|replaceInvalidAcronym
expr_stmt|;
block|}
block|}
end_class
end_unit
