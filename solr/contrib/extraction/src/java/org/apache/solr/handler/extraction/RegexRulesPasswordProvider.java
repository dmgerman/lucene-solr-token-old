begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.extraction
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|extraction
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|PatternSyntaxException
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
name|IOUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|TikaMetadataKeys
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|PasswordProvider
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_comment
comment|/**  * Password provider for Extracting request handler which finds correct  * password based on file name matching against a list of regular expressions.   * The list of passwords is supplied in an optional Map.  * If an explicit password is set, it will be used.  */
end_comment
begin_class
DECL|class|RegexRulesPasswordProvider
specifier|public
class|class
name|RegexRulesPasswordProvider
implements|implements
name|PasswordProvider
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RegexRulesPasswordProvider
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|passwordMap
specifier|private
name|LinkedHashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|passwordMap
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|explicitPassword
specifier|private
name|String
name|explicitPassword
decl_stmt|;
annotation|@
name|Override
DECL|method|getPassword
specifier|public
name|String
name|getPassword
parameter_list|(
name|Metadata
name|meta
parameter_list|)
block|{
if|if
condition|(
name|getExplicitPassword
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|getExplicitPassword
argument_list|()
return|;
block|}
if|if
condition|(
name|passwordMap
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
return|return
name|lookupPasswordFromMap
argument_list|(
name|meta
operator|.
name|get
argument_list|(
name|TikaMetadataKeys
operator|.
name|RESOURCE_NAME_KEY
argument_list|)
argument_list|)
return|;
return|return
literal|null
return|;
block|}
DECL|method|lookupPasswordFromMap
specifier|private
name|String
name|lookupPasswordFromMap
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
if|if
condition|(
name|fileName
operator|!=
literal|null
operator|&&
name|fileName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|e
range|:
name|passwordMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|matcher
argument_list|(
name|fileName
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|e
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Parses rule file from stream and returns a Map of all rules found    * @param is input stream for the file    */
DECL|method|parseRulesFile
specifier|public
specifier|static
name|LinkedHashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|parseRulesFile
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|LinkedHashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|rules
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|is
argument_list|,
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
try|try
block|{
name|int
name|linenum
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|linenum
operator|++
expr_stmt|;
comment|// Remove comments
name|String
index|[]
name|arr
init|=
name|line
operator|.
name|split
argument_list|(
literal|"#"
argument_list|)
decl_stmt|;
if|if
condition|(
name|arr
operator|.
name|length
operator|>
literal|0
condition|)
name|line
operator|=
name|arr
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|int
name|sep
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|sep
operator|<=
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Wrong format of password line "
operator|+
name|linenum
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
name|pass
init|=
name|line
operator|.
name|substring
argument_list|(
name|sep
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|regex
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sep
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
try|try
block|{
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|)
decl_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|pattern
argument_list|,
name|pass
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|pse
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Key of line "
operator|+
name|linenum
operator|+
literal|" was not a valid regex pattern"
argument_list|,
name|pse
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|()
throw|;
block|}
return|return
name|rules
return|;
block|}
comment|/**    * Initialize rules through file input stream. This is a convenience for first calling    * setPasswordMap(parseRulesFile(is)).    * @param is the input stream with rules file, one line per rule on format regex=password    */
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|setPasswordMap
argument_list|(
name|parseRulesFile
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getPasswordMap
specifier|public
name|LinkedHashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|getPasswordMap
parameter_list|()
block|{
return|return
name|passwordMap
return|;
block|}
DECL|method|setPasswordMap
specifier|public
name|void
name|setPasswordMap
parameter_list|(
name|LinkedHashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|linkedHashMap
parameter_list|)
block|{
name|this
operator|.
name|passwordMap
operator|=
name|linkedHashMap
expr_stmt|;
block|}
comment|/**    * Gets the explicit password, if set    * @return the password, or null if not set    */
DECL|method|getExplicitPassword
specifier|public
name|String
name|getExplicitPassword
parameter_list|()
block|{
return|return
name|explicitPassword
return|;
block|}
comment|/**    * Sets an explicit password which will be used instead of password map    * @param explicitPassword the password to use    */
DECL|method|setExplicitPassword
specifier|public
name|void
name|setExplicitPassword
parameter_list|(
name|String
name|explicitPassword
parameter_list|)
block|{
name|this
operator|.
name|explicitPassword
operator|=
name|explicitPassword
expr_stmt|;
block|}
comment|/**    * Resets explicit password, so that map will be used for lookups    */
DECL|method|resetExplicitPassword
specifier|public
name|void
name|resetExplicitPassword
parameter_list|()
block|{
name|this
operator|.
name|explicitPassword
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
