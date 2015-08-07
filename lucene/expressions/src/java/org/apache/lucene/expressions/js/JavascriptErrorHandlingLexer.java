begin_unit
begin_package
DECL|package|org.apache.lucene.expressions.js
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
operator|.
name|js
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|CharStream
import|;
end_import
begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|LexerNoViableAltException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|antlr
operator|.
name|v4
operator|.
name|runtime
operator|.
name|misc
operator|.
name|Interval
import|;
end_import
begin_comment
comment|/**  * Overrides the ANTLR 4 generated JavascriptLexer to allow for proper error handling  */
end_comment
begin_class
DECL|class|JavascriptErrorHandlingLexer
class|class
name|JavascriptErrorHandlingLexer
extends|extends
name|JavascriptLexer
block|{
comment|/**    * Constructor for JavascriptErrorHandlingLexer    * @param charStream the stream for the source text    */
DECL|method|JavascriptErrorHandlingLexer
specifier|public
name|JavascriptErrorHandlingLexer
parameter_list|(
name|CharStream
name|charStream
parameter_list|)
block|{
name|super
argument_list|(
name|charStream
argument_list|)
expr_stmt|;
block|}
comment|/**    * Ensures the ANTLR lexer will throw an exception after the first error    * @param lnvae the lexer exception    */
annotation|@
name|Override
DECL|method|recover
specifier|public
name|void
name|recover
parameter_list|(
name|LexerNoViableAltException
name|lnvae
parameter_list|)
block|{
name|CharStream
name|charStream
init|=
name|lnvae
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|startIndex
init|=
name|lnvae
operator|.
name|getStartIndex
argument_list|()
decl_stmt|;
name|String
name|text
init|=
name|charStream
operator|.
name|getText
argument_list|(
name|Interval
operator|.
name|of
argument_list|(
name|startIndex
argument_list|,
name|charStream
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ParseException
name|parseException
init|=
operator|new
name|ParseException
argument_list|(
literal|"unexpected character '"
operator|+
name|getErrorDisplay
argument_list|(
name|text
argument_list|)
operator|+
literal|"'"
operator|+
literal|" on line ("
operator|+
name|_tokenStartLine
operator|+
literal|") position ("
operator|+
name|_tokenStartCharPositionInLine
operator|+
literal|")"
argument_list|,
name|_tokenStartCharIndex
argument_list|)
decl_stmt|;
name|parseException
operator|.
name|initCause
argument_list|(
name|lnvae
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|parseException
argument_list|)
throw|;
block|}
block|}
end_class
end_unit
