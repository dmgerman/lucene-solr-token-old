begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|SEVERE
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DataImportHandlerException
operator|.
name|wrapAndThrow
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|DocBuilder
operator|.
name|loadClass
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|config
operator|.
name|ConfigNameConstants
operator|.
name|CLASS
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
operator|.
name|config
operator|.
name|ConfigNameConstants
operator|.
name|NAME
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|DateMathParser
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|util
operator|.
name|ClientUtils
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
begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLEncoder
import|;
end_import
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
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
comment|/**  *<p> Holds definitions for evaluators provided by DataImportHandler</p><p/><p> Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a> for more  * details.</p>  *<p/>  *<b>This API is experimental and may change in the future.</b>  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|EvaluatorBag
specifier|public
class|class
name|EvaluatorBag
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EvaluatorBag
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DATE_FORMAT_EVALUATOR
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FORMAT_EVALUATOR
init|=
literal|"formatDate"
decl_stmt|;
DECL|field|URL_ENCODE_EVALUATOR
specifier|public
specifier|static
specifier|final
name|String
name|URL_ENCODE_EVALUATOR
init|=
literal|"encodeUrl"
decl_stmt|;
DECL|field|ESCAPE_SOLR_QUERY_CHARS
specifier|public
specifier|static
specifier|final
name|String
name|ESCAPE_SOLR_QUERY_CHARS
init|=
literal|"escapeQueryChars"
decl_stmt|;
DECL|field|SQL_ESCAPE_EVALUATOR
specifier|public
specifier|static
specifier|final
name|String
name|SQL_ESCAPE_EVALUATOR
init|=
literal|"escapeSql"
decl_stmt|;
DECL|field|FORMAT_METHOD
specifier|static
specifier|final
name|Pattern
name|FORMAT_METHOD
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(\\w*?)\\((.*?)\\)$"
argument_list|)
decl_stmt|;
comment|/**    *<p/> Returns an<code>Evaluator</code> instance meant to be used for escaping values in SQL queries.</p><p/> It    * escapes the value of the given expression by replacing all occurrences of single-quotes by two single-quotes and    * similarily for double-quotes</p>    *    * @return an<code>Evaluator</code> instance capable of SQL-escaping expressions.    */
DECL|method|getSqlEscapingEvaluator
specifier|public
specifier|static
name|Evaluator
name|getSqlEscapingEvaluator
parameter_list|()
block|{
return|return
operator|new
name|Evaluator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|evaluate
parameter_list|(
name|String
name|expression
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|List
name|l
init|=
name|parseParams
argument_list|(
name|expression
argument_list|,
name|context
operator|.
name|getVariableResolver
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"'escapeSql' must have at least one parameter "
argument_list|)
throw|;
block|}
name|String
name|s
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// escape single quote with two single quotes, double quote
comment|// with two doule quotes, and backslash with double backslash.
comment|// See:  http://dev.mysql.com/doc/refman/4.1/en/mysql-real-escape-string.html
return|return
name|s
operator|.
name|replaceAll
argument_list|(
literal|"'"
argument_list|,
literal|"''"
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|"\"\""
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"\\\\\\\\"
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    *<p/>Returns an<code>Evaluator</code> instance meant to be used for escaping reserved characters in Solr    * queries</p>    *    * @return an<code>Evaluator</code> instance capable of escaping reserved characters in solr queries.    *    * @see org.apache.solr.client.solrj.util.ClientUtils#escapeQueryChars(String)    */
DECL|method|getSolrQueryEscapingEvaluator
specifier|public
specifier|static
name|Evaluator
name|getSolrQueryEscapingEvaluator
parameter_list|()
block|{
return|return
operator|new
name|Evaluator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|evaluate
parameter_list|(
name|String
name|expression
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|List
name|l
init|=
name|parseParams
argument_list|(
name|expression
argument_list|,
name|context
operator|.
name|getVariableResolver
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"'escapeQueryChars' must have at least one parameter "
argument_list|)
throw|;
block|}
name|String
name|s
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
name|ClientUtils
operator|.
name|escapeQueryChars
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    *<p/> Returns an<code>Evaluator</code> instance capable of URL-encoding expressions. The expressions are evaluated    * using a<code>VariableResolver</code></p>    *    * @return an<code>Evaluator</code> instance capable of URL-encoding expressions.    */
DECL|method|getUrlEvaluator
specifier|public
specifier|static
name|Evaluator
name|getUrlEvaluator
parameter_list|()
block|{
return|return
operator|new
name|Evaluator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|evaluate
parameter_list|(
name|String
name|expression
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|List
name|l
init|=
name|parseParams
argument_list|(
name|expression
argument_list|,
name|context
operator|.
name|getVariableResolver
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"'encodeUrl' must have at least one parameter "
argument_list|)
throw|;
block|}
name|String
name|s
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|URLEncoder
operator|.
name|encode
argument_list|(
name|s
operator|.
name|toString
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Unable to encode expression: "
operator|+
name|expression
operator|+
literal|" with value: "
operator|+
name|s
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
return|;
block|}
comment|/**    *<p/> Returns an<code>Evaluator</code> instance capable of formatting values using a given date format.</p><p/>    * The value to be formatted can be a entity.field or a date expression parsed with<code>DateMathParser</code> class.    * If the value is in a String, then it is assumed to be a datemath expression, otherwise it resolved using a    *<code>VariableResolver</code> instance</p>    *    * @return an Evaluator instance capable of formatting values to a given date format    *    * @see DateMathParser    */
DECL|method|getDateFormatEvaluator
specifier|public
specifier|static
name|Evaluator
name|getDateFormatEvaluator
parameter_list|()
block|{
return|return
operator|new
name|Evaluator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|evaluate
parameter_list|(
name|String
name|expression
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
name|List
name|l
init|=
name|parseParams
argument_list|(
name|expression
argument_list|,
name|context
operator|.
name|getVariableResolver
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"'formatDate()' must have two parameters "
argument_list|)
throw|;
block|}
name|Object
name|o
init|=
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Object
name|format
init|=
name|l
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|format
operator|instanceof
name|VariableWrapper
condition|)
block|{
name|VariableWrapper
name|wrapper
init|=
operator|(
name|VariableWrapper
operator|)
name|format
decl_stmt|;
name|o
operator|=
name|wrapper
operator|.
name|resolve
argument_list|()
expr_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|format
operator|=
name|wrapper
operator|.
name|varName
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Deprecated syntax used. The syntax of formatDate has been changed to formatDate(<var>, '<date_format_string>'). "
operator|+
literal|"The old syntax will stop working in Solr 1.5"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|format
operator|=
name|o
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
name|String
name|dateFmt
init|=
name|format
operator|.
name|toString
argument_list|()
decl_stmt|;
name|SimpleDateFormat
name|fmt
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|dateFmt
argument_list|)
decl_stmt|;
name|Date
name|date
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|VariableWrapper
condition|)
block|{
name|VariableWrapper
name|variableWrapper
init|=
operator|(
name|VariableWrapper
operator|)
name|o
decl_stmt|;
name|Object
name|variableval
init|=
name|variableWrapper
operator|.
name|resolve
argument_list|()
decl_stmt|;
if|if
condition|(
name|variableval
operator|instanceof
name|Date
condition|)
block|{
name|date
operator|=
operator|(
name|Date
operator|)
name|variableval
expr_stmt|;
block|}
else|else
block|{
name|String
name|s
init|=
name|variableval
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|date
operator|=
name|DataImporter
operator|.
name|DATE_TIME_FORMAT
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|exp
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|exp
argument_list|,
literal|"Invalid expression for date"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|String
name|datemathfmt
init|=
name|o
operator|.
name|toString
argument_list|()
decl_stmt|;
name|datemathfmt
operator|=
name|datemathfmt
operator|.
name|replaceAll
argument_list|(
literal|"NOW"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
try|try
block|{
name|date
operator|=
name|dateMathParser
operator|.
name|parseMath
argument_list|(
name|datemathfmt
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Invalid expression for date"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fmt
operator|.
name|format
argument_list|(
name|date
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|getFunctionsNamespace
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getFunctionsNamespace
parameter_list|(
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|fn
parameter_list|,
name|DocBuilder
name|docBuilder
parameter_list|,
specifier|final
name|VariableResolverImpl
name|vr
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Evaluator
argument_list|>
name|evaluators
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Evaluator
argument_list|>
argument_list|()
decl_stmt|;
name|evaluators
operator|.
name|put
argument_list|(
name|DATE_FORMAT_EVALUATOR
argument_list|,
name|getDateFormatEvaluator
argument_list|()
argument_list|)
expr_stmt|;
name|evaluators
operator|.
name|put
argument_list|(
name|SQL_ESCAPE_EVALUATOR
argument_list|,
name|getSqlEscapingEvaluator
argument_list|()
argument_list|)
expr_stmt|;
name|evaluators
operator|.
name|put
argument_list|(
name|URL_ENCODE_EVALUATOR
argument_list|,
name|getUrlEvaluator
argument_list|()
argument_list|)
expr_stmt|;
name|evaluators
operator|.
name|put
argument_list|(
name|ESCAPE_SOLR_QUERY_CHARS
argument_list|,
name|getSolrQueryEscapingEvaluator
argument_list|()
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|docBuilder
operator|==
literal|null
condition|?
literal|null
else|:
name|docBuilder
operator|.
name|dataImporter
operator|.
name|getCore
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
range|:
name|fn
control|)
block|{
try|try
block|{
name|evaluators
operator|.
name|put
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|NAME
argument_list|)
argument_list|,
operator|(
name|Evaluator
operator|)
name|loadClass
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|CLASS
argument_list|)
argument_list|,
name|core
argument_list|)
operator|.
name|newInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Unable to instantiate evaluator: "
operator|+
name|map
operator|.
name|get
argument_list|(
name|CLASS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Matcher
name|m
init|=
name|FORMAT_METHOD
operator|.
name|matcher
argument_list|(
operator|(
name|String
operator|)
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|find
argument_list|()
condition|)
return|return
literal|null
return|;
name|String
name|fname
init|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Evaluator
name|evaluator
init|=
name|evaluators
operator|.
name|get
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
name|evaluator
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|ContextImpl
name|ctx
init|=
operator|new
name|ContextImpl
argument_list|(
literal|null
argument_list|,
name|vr
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|g2
init|=
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
return|return
name|evaluator
operator|.
name|evaluate
argument_list|(
name|g2
argument_list|,
name|ctx
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Parses a string of expression into separate params. The values are separated by commas. each value will be    * translated into one of the following:    *&lt;ol&gt;    *&lt;li&gt;If it is in single quotes the value will be translated to a String&lt;/li&gt;    *&lt;li&gt;If is is not in quotes and is a number a it will be translated into a Double&lt;/li&gt;    *&lt;li&gt;else it is a variable which can be resolved and it will be put in as an instance of VariableWrapper&lt;/li&gt;    *&lt;/ol&gt;    *    * @param expression the expression to be parsed    * @param vr the VariableResolver instance for resolving variables    *    * @return a List of objects which can either be a string, number or a variable wrapper    */
DECL|method|parseParams
specifier|public
specifier|static
name|List
name|parseParams
parameter_list|(
name|String
name|expression
parameter_list|,
name|VariableResolver
name|vr
parameter_list|)
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|expression
operator|=
name|expression
operator|.
name|trim
argument_list|()
expr_stmt|;
name|String
index|[]
name|ss
init|=
name|expression
operator|.
name|split
argument_list|(
literal|","
argument_list|)
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
name|ss
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ss
index|[
name|i
index|]
operator|=
name|ss
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|ss
index|[
name|i
index|]
operator|.
name|startsWith
argument_list|(
literal|"'"
argument_list|)
condition|)
block|{
comment|//a string param has started
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|ss
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|ss
index|[
name|i
index|]
operator|.
name|endsWith
argument_list|(
literal|"'"
argument_list|)
condition|)
break|break;
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|ss
operator|.
name|length
condition|)
throw|throw
operator|new
name|DataImportHandlerException
argument_list|(
name|SEVERE
argument_list|,
literal|"invalid string at "
operator|+
name|ss
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|" in function params: "
operator|+
name|expression
argument_list|)
throw|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|String
name|s
init|=
name|sb
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|s
operator|=
name|s
operator|.
name|replaceAll
argument_list|(
literal|"\\\\'"
argument_list|,
literal|"'"
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|ss
index|[
name|i
index|]
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
try|try
block|{
name|Double
name|doub
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|ss
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|doub
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
if|if
condition|(
name|vr
operator|.
name|resolve
argument_list|(
name|ss
index|[
name|i
index|]
argument_list|)
operator|==
literal|null
condition|)
block|{
name|wrapAndThrow
argument_list|(
name|SEVERE
argument_list|,
name|e
argument_list|,
literal|"Invalid number :"
operator|+
name|ss
index|[
name|i
index|]
operator|+
literal|"in parameters  "
operator|+
name|expression
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|VariableWrapper
argument_list|(
name|ss
index|[
name|i
index|]
argument_list|,
name|vr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
DECL|class|VariableWrapper
specifier|public
specifier|static
class|class
name|VariableWrapper
block|{
DECL|field|varName
name|String
name|varName
decl_stmt|;
DECL|field|vr
name|VariableResolver
name|vr
decl_stmt|;
DECL|method|VariableWrapper
specifier|public
name|VariableWrapper
parameter_list|(
name|String
name|s
parameter_list|,
name|VariableResolver
name|vr
parameter_list|)
block|{
name|this
operator|.
name|varName
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|vr
operator|=
name|vr
expr_stmt|;
block|}
DECL|method|resolve
specifier|public
name|Object
name|resolve
parameter_list|()
block|{
return|return
name|vr
operator|.
name|resolve
argument_list|(
name|varName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|Object
name|o
init|=
name|vr
operator|.
name|resolve
argument_list|(
name|varName
argument_list|)
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|null
else|:
name|o
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
DECL|field|IN_SINGLE_QUOTES
specifier|static
name|Pattern
name|IN_SINGLE_QUOTES
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^'(.*?)'$"
argument_list|)
decl_stmt|;
DECL|field|dateMathParser
specifier|static
name|DateMathParser
name|dateMathParser
init|=
operator|new
name|DateMathParser
argument_list|(
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Date
name|getNow
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|()
return|;
block|}
block|}
decl_stmt|;
block|}
end_class
end_unit
