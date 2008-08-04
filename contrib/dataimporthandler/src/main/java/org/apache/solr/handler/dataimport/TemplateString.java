begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import
begin_comment
comment|/**  *<p>  * Provides functionality for replacing variables in a templatized string. It  * can also be used to get the place-holders (variables) in a templatized  * string.  *</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TemplateString
specifier|public
class|class
name|TemplateString
block|{
DECL|field|variables
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|variables
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|pcs
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|pcs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|cache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|TemplateString
argument_list|>
name|cache
decl_stmt|;
DECL|method|TemplateString
specifier|public
name|TemplateString
parameter_list|()
block|{
name|cache
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TemplateString
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|TemplateString
specifier|private
name|TemplateString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|Matcher
name|m
init|=
name|WORD_PATTERN
operator|.
name|matcher
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|aparam
init|=
name|s
operator|.
name|substring
argument_list|(
name|m
operator|.
name|start
argument_list|()
operator|+
literal|2
argument_list|,
name|m
operator|.
name|end
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|variables
operator|.
name|add
argument_list|(
name|aparam
argument_list|)
expr_stmt|;
name|pcs
operator|.
name|add
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|idx
argument_list|,
name|m
operator|.
name|start
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|idx
operator|=
name|m
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
name|pcs
operator|.
name|add
argument_list|(
name|s
operator|.
name|substring
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a string with all variables replaced by the known values. An    * unknown variable is replaced by an empty string.    *    * @param string the String to be resolved    * @param resolver the VariableResolver instance to be used for evaluation    * @return the string with all variables replaced    */
DECL|method|replaceTokens
specifier|public
name|String
name|replaceTokens
parameter_list|(
name|String
name|string
parameter_list|,
name|VariableResolver
name|resolver
parameter_list|)
block|{
name|TemplateString
name|ts
init|=
name|cache
operator|.
name|get
argument_list|(
name|string
argument_list|)
decl_stmt|;
if|if
condition|(
name|ts
operator|==
literal|null
condition|)
block|{
name|ts
operator|=
operator|new
name|TemplateString
argument_list|(
name|string
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|string
argument_list|,
name|ts
argument_list|)
expr_stmt|;
block|}
return|return
name|ts
operator|.
name|fillTokens
argument_list|(
name|resolver
argument_list|)
return|;
block|}
DECL|method|fillTokens
specifier|private
name|String
name|fillTokens
parameter_list|(
name|VariableResolver
name|resolver
parameter_list|)
block|{
name|String
index|[]
name|s
init|=
operator|new
name|String
index|[
name|variables
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|variables
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|val
init|=
name|resolver
operator|.
name|resolve
argument_list|(
name|variables
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|s
index|[
name|i
index|]
operator|=
name|val
operator|==
literal|null
condition|?
literal|""
else|:
name|getObjectAsString
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pcs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|pcs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|s
operator|.
name|length
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|s
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getObjectAsString
specifier|private
name|String
name|getObjectAsString
parameter_list|(
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|instanceof
name|java
operator|.
name|sql
operator|.
name|Date
condition|)
block|{
name|java
operator|.
name|sql
operator|.
name|Date
name|d
init|=
operator|(
name|java
operator|.
name|sql
operator|.
name|Date
operator|)
name|val
decl_stmt|;
return|return
name|DataImporter
operator|.
name|DATE_TIME_FORMAT
operator|.
name|format
argument_list|(
name|d
argument_list|)
return|;
block|}
return|return
name|val
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns the variables in the given string.    *    * @param s the templatized string    * @return the list of variables (strings) in the given templatized string.    */
DECL|method|getVariables
specifier|public
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getVariables
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|TemplateString
argument_list|(
name|s
argument_list|)
operator|.
name|variables
return|;
block|}
DECL|field|WORD_PATTERN
specifier|static
specifier|final
name|Pattern
name|WORD_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\$\\{.*?\\})"
argument_list|)
decl_stmt|;
block|}
end_class
end_unit
