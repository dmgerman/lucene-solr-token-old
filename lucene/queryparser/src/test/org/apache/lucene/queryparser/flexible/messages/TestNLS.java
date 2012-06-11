begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.messages
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|messages
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  */
end_comment
begin_class
DECL|class|TestNLS
specifier|public
class|class
name|TestNLS
extends|extends
name|LuceneTestCase
block|{
DECL|method|testMessageLoading
specifier|public
name|void
name|testMessageLoading
parameter_list|()
block|{
name|Message
name|invalidSyntax
init|=
operator|new
name|MessageImpl
argument_list|(
name|MessagesTestBundle
operator|.
name|Q0001E_INVALID_SYNTAX
argument_list|,
literal|"XXX"
argument_list|)
decl_stmt|;
comment|/*       * if the default locale is ja, you get ja as a fallback:      * see ResourceBundle.html#getBundle(java.lang.String, java.util.Locale, java.lang.ClassLoader)      */
if|if
condition|(
operator|!
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ja"
argument_list|)
condition|)
name|assertEquals
argument_list|(
literal|"Syntax Error: XXX"
argument_list|,
name|invalidSyntax
operator|.
name|getLocalizedMessage
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMessageLoading_ja
specifier|public
name|void
name|testMessageLoading_ja
parameter_list|()
block|{
name|Message
name|invalidSyntax
init|=
operator|new
name|MessageImpl
argument_list|(
name|MessagesTestBundle
operator|.
name|Q0001E_INVALID_SYNTAX
argument_list|,
literal|"XXX"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"æ§æã¨ã©ã¼: XXX"
argument_list|,
name|invalidSyntax
operator|.
name|getLocalizedMessage
argument_list|(
name|Locale
operator|.
name|JAPANESE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNLSLoading
specifier|public
name|void
name|testNLSLoading
parameter_list|()
block|{
name|String
name|message
init|=
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|MessagesTestBundle
operator|.
name|Q0004E_INVALID_SYNTAX_ESCAPE_UNICODE_TRUNCATION
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
comment|/*       * if the default locale is ja, you get ja as a fallback:      * see ResourceBundle.html#getBundle(java.lang.String, java.util.Locale, java.lang.ClassLoader)      */
if|if
condition|(
operator|!
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ja"
argument_list|)
condition|)
name|assertEquals
argument_list|(
literal|"Truncated unicode escape sequence."
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|MessagesTestBundle
operator|.
name|Q0001E_INVALID_SYNTAX
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"XXX"
argument_list|)
expr_stmt|;
comment|/*       * if the default locale is ja, you get ja as a fallback:      * see ResourceBundle.html#getBundle(java.lang.String, java.util.Locale, java.lang.ClassLoader)      */
if|if
condition|(
operator|!
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ja"
argument_list|)
condition|)
name|assertEquals
argument_list|(
literal|"Syntax Error: XXX"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|testNLSLoading_ja
specifier|public
name|void
name|testNLSLoading_ja
parameter_list|()
block|{
name|String
name|message
init|=
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|MessagesTestBundle
operator|.
name|Q0004E_INVALID_SYNTAX_ESCAPE_UNICODE_TRUNCATION
argument_list|,
name|Locale
operator|.
name|JAPANESE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"åãæ¨ã¦ãããã¦ãã³ã¼ãã»ã¨ã¹ã±ã¼ãã»ã·ã¼ã±ã³ã¹ã"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|MessagesTestBundle
operator|.
name|Q0001E_INVALID_SYNTAX
argument_list|,
name|Locale
operator|.
name|JAPANESE
argument_list|,
literal|"XXX"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"æ§æã¨ã©ã¼: XXX"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|testNLSLoading_xx_XX
specifier|public
name|void
name|testNLSLoading_xx_XX
parameter_list|()
block|{
name|Locale
name|locale
init|=
operator|new
name|Locale
argument_list|(
literal|"xx"
argument_list|,
literal|"XX"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|String
name|message
init|=
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|MessagesTestBundle
operator|.
name|Q0004E_INVALID_SYNTAX_ESCAPE_UNICODE_TRUNCATION
argument_list|,
name|locale
argument_list|)
decl_stmt|;
comment|/*       * if the default locale is ja, you get ja as a fallback:      * see ResourceBundle.html#getBundle(java.lang.String, java.util.Locale, java.lang.ClassLoader)      */
if|if
condition|(
operator|!
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ja"
argument_list|)
condition|)
name|assertEquals
argument_list|(
literal|"Truncated unicode escape sequence."
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|message
operator|=
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|MessagesTestBundle
operator|.
name|Q0001E_INVALID_SYNTAX
argument_list|,
name|locale
argument_list|,
literal|"XXX"
argument_list|)
expr_stmt|;
comment|/*       * if the default locale is ja, you get ja as a fallback:      * see ResourceBundle.html#getBundle(java.lang.String, java.util.Locale, java.lang.ClassLoader)      */
if|if
condition|(
operator|!
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|getLanguage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ja"
argument_list|)
condition|)
name|assertEquals
argument_list|(
literal|"Syntax Error: XXX"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|testMissingMessage
specifier|public
name|void
name|testMissingMessage
parameter_list|()
block|{
name|Locale
name|locale
init|=
name|Locale
operator|.
name|ENGLISH
decl_stmt|;
name|String
name|message
init|=
name|NLS
operator|.
name|getLocalizedMessage
argument_list|(
name|MessagesTestBundle
operator|.
name|Q0005E_MESSAGE_NOT_IN_BUNDLE
argument_list|,
name|locale
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Message with key:Q0005E_MESSAGE_NOT_IN_BUNDLE and locale: "
operator|+
name|locale
operator|.
name|toString
argument_list|()
operator|+
literal|" not found."
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
