begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
begin_comment
comment|/**  *<p> Test for Evaluators</p>  *  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|TestBuiltInEvaluators
specifier|public
class|class
name|TestBuiltInEvaluators
extends|extends
name|AbstractDataImportHandlerTestCase
block|{
DECL|field|ENCODING
specifier|private
specifier|static
specifier|final
name|String
name|ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
DECL|field|resolver
name|VariableResolver
name|resolver
decl_stmt|;
DECL|field|sqlTests
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sqlTests
decl_stmt|;
DECL|field|urlTests
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|urlTests
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|resolver
operator|=
operator|new
name|VariableResolver
argument_list|()
expr_stmt|;
name|sqlTests
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|sqlTests
operator|.
name|put
argument_list|(
literal|"foo\""
argument_list|,
literal|"foo\"\""
argument_list|)
expr_stmt|;
name|sqlTests
operator|.
name|put
argument_list|(
literal|"foo\\"
argument_list|,
literal|"foo\\\\"
argument_list|)
expr_stmt|;
name|sqlTests
operator|.
name|put
argument_list|(
literal|"foo'"
argument_list|,
literal|"foo''"
argument_list|)
expr_stmt|;
name|sqlTests
operator|.
name|put
argument_list|(
literal|"foo''"
argument_list|,
literal|"foo''''"
argument_list|)
expr_stmt|;
name|sqlTests
operator|.
name|put
argument_list|(
literal|"'foo\""
argument_list|,
literal|"''foo\"\""
argument_list|)
expr_stmt|;
name|sqlTests
operator|.
name|put
argument_list|(
literal|"\"Albert D'souza\""
argument_list|,
literal|"\"\"Albert D''souza\"\""
argument_list|)
expr_stmt|;
name|urlTests
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|urlTests
operator|.
name|put
argument_list|(
literal|"*:*"
argument_list|,
name|URLEncoder
operator|.
name|encode
argument_list|(
literal|"*:*"
argument_list|,
name|ENCODING
argument_list|)
argument_list|)
expr_stmt|;
name|urlTests
operator|.
name|put
argument_list|(
literal|"price:[* TO 200]"
argument_list|,
name|URLEncoder
operator|.
name|encode
argument_list|(
literal|"price:[* TO 200]"
argument_list|,
name|ENCODING
argument_list|)
argument_list|)
expr_stmt|;
name|urlTests
operator|.
name|put
argument_list|(
literal|"review:\"hybrid sedan\""
argument_list|,
name|URLEncoder
operator|.
name|encode
argument_list|(
literal|"review:\"hybrid sedan\""
argument_list|,
name|ENCODING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSqlEscapingEvaluator
specifier|public
name|void
name|testSqlEscapingEvaluator
parameter_list|()
block|{
name|Evaluator
name|sqlEscaper
init|=
operator|new
name|SqlEscapingEvaluator
argument_list|()
decl_stmt|;
name|runTests
argument_list|(
name|sqlTests
argument_list|,
name|sqlEscaper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUrlEvaluator
specifier|public
name|void
name|testUrlEvaluator
parameter_list|()
throws|throws
name|Exception
block|{
name|Evaluator
name|urlEvaluator
init|=
operator|new
name|UrlEvaluator
argument_list|()
decl_stmt|;
name|runTests
argument_list|(
name|urlTests
argument_list|,
name|urlEvaluator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|parseParams
specifier|public
name|void
name|parseParams
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
literal|"B"
argument_list|)
expr_stmt|;
name|VariableResolver
name|vr
init|=
operator|new
name|VariableResolver
argument_list|()
decl_stmt|;
name|vr
operator|.
name|addNamespace
argument_list|(
literal|"a"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|l
init|=
operator|(
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
return|return
literal|null
return|;
block|}
block|}
operator|)
operator|.
name|parseParams
argument_list|(
literal|" 1 , a.b, 'hello!', 'ds,o,u\'za',"
argument_list|,
name|vr
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|Double
argument_list|(
literal|1
argument_list|)
argument_list|,
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"B"
argument_list|,
operator|(
operator|(
name|Evaluator
operator|.
name|VariableWrapper
operator|)
name|l
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|resolve
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hello!"
argument_list|,
name|l
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"ds,o,u'za"
argument_list|,
name|l
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEscapeSolrQueryFunction
specifier|public
name|void
name|testEscapeSolrQueryFunction
parameter_list|()
block|{
specifier|final
name|VariableResolver
name|resolver
init|=
operator|new
name|VariableResolver
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|m
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
literal|"c:t"
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|setEvaluators
argument_list|(
operator|new
name|DataImporter
argument_list|()
operator|.
name|getEvaluators
argument_list|(
name|Collections
operator|.
expr|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|addNamespace
argument_list|(
literal|"e"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|resolver
operator|.
name|replaceTokens
argument_list|(
literal|"${dataimporter.functions.escapeQueryChars(e.query)}"
argument_list|)
decl_stmt|;
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"c\\:t"
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|twoDaysAgo
specifier|private
name|Date
name|twoDaysAgo
parameter_list|(
name|Locale
name|l
parameter_list|,
name|TimeZone
name|tz
parameter_list|)
block|{
name|Calendar
name|calendar
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|tz
argument_list|,
name|l
argument_list|)
decl_stmt|;
name|calendar
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|DAY_OF_YEAR
argument_list|,
operator|-
literal|2
argument_list|)
expr_stmt|;
return|return
name|calendar
operator|.
name|getTime
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testDateFormatEvaluator
specifier|public
name|void
name|testDateFormatEvaluator
parameter_list|()
block|{
name|Evaluator
name|dateFormatEval
init|=
operator|new
name|DateFormatEvaluator
argument_list|()
decl_stmt|;
name|ContextImpl
name|context
init|=
operator|new
name|ContextImpl
argument_list|(
literal|null
argument_list|,
name|resolver
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Locale
name|rootLocale
init|=
name|Locale
operator|.
name|ROOT
decl_stmt|;
name|Locale
name|defaultLocale
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|TimeZone
name|defaultTz
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
decl_stmt|;
block|{
name|SimpleDateFormat
name|sdfDate
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH"
argument_list|,
name|rootLocale
argument_list|)
decl_stmt|;
name|String
name|sdf
init|=
name|sdfDate
operator|.
name|format
argument_list|(
name|twoDaysAgo
argument_list|(
name|rootLocale
argument_list|,
name|defaultTz
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|dfe
init|=
name|dateFormatEval
operator|.
name|evaluate
argument_list|(
literal|"'NOW-2DAYS','yyyy-MM-dd HH'"
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sdf
argument_list|,
name|dfe
argument_list|)
expr_stmt|;
block|}
block|{
name|SimpleDateFormat
name|sdfDate
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH"
argument_list|,
name|defaultLocale
argument_list|)
decl_stmt|;
name|String
name|sdf
init|=
name|sdfDate
operator|.
name|format
argument_list|(
name|twoDaysAgo
argument_list|(
name|defaultLocale
argument_list|,
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|dfe
init|=
name|dateFormatEval
operator|.
name|evaluate
argument_list|(
literal|"'NOW-2DAYS','yyyy-MM-dd HH','"
operator|+
name|defaultLocale
operator|+
literal|"'"
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sdf
argument_list|,
name|dfe
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|tzStr
range|:
name|TimeZone
operator|.
name|getAvailableIDs
argument_list|()
control|)
block|{
name|TimeZone
name|tz
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|tzStr
argument_list|)
decl_stmt|;
name|sdfDate
operator|.
name|setTimeZone
argument_list|(
name|tz
argument_list|)
expr_stmt|;
name|sdf
operator|=
name|sdfDate
operator|.
name|format
argument_list|(
name|twoDaysAgo
argument_list|(
name|defaultLocale
argument_list|,
name|tz
argument_list|)
argument_list|)
expr_stmt|;
name|dfe
operator|=
name|dateFormatEval
operator|.
name|evaluate
argument_list|(
literal|"'NOW-2DAYS','yyyy-MM-dd HH','"
operator|+
name|defaultLocale
operator|+
literal|"','"
operator|+
name|tzStr
operator|+
literal|"'"
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sdf
argument_list|,
name|dfe
argument_list|)
expr_stmt|;
block|}
block|}
name|Date
name|d
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
name|d
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|addNamespace
argument_list|(
literal|"A"
argument_list|,
name|map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm"
argument_list|,
name|rootLocale
argument_list|)
operator|.
name|format
argument_list|(
name|d
argument_list|)
argument_list|,
name|dateFormatEval
operator|.
name|evaluate
argument_list|(
literal|"A.key, 'yyyy-MM-dd HH:mm'"
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm"
argument_list|,
name|defaultLocale
argument_list|)
operator|.
name|format
argument_list|(
name|d
argument_list|)
argument_list|,
name|dateFormatEval
operator|.
name|evaluate
argument_list|(
literal|"A.key, 'yyyy-MM-dd HH:mm','"
operator|+
name|defaultLocale
operator|+
literal|"'"
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd HH:mm"
argument_list|,
name|defaultLocale
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|tzStr
range|:
name|TimeZone
operator|.
name|getAvailableIDs
argument_list|()
control|)
block|{
name|TimeZone
name|tz
init|=
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
name|tzStr
argument_list|)
decl_stmt|;
name|sdf
operator|.
name|setTimeZone
argument_list|(
name|tz
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sdf
operator|.
name|format
argument_list|(
name|d
argument_list|)
argument_list|,
name|dateFormatEval
operator|.
name|evaluate
argument_list|(
literal|"A.key, 'yyyy-MM-dd HH:mm','"
operator|+
name|defaultLocale
operator|+
literal|"', '"
operator|+
name|tzStr
operator|+
literal|"'"
argument_list|,
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|runTests
specifier|private
name|void
name|runTests
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tests
parameter_list|,
name|Evaluator
name|evaluator
parameter_list|)
block|{
name|ContextImpl
name|ctx
init|=
operator|new
name|ContextImpl
argument_list|(
literal|null
argument_list|,
name|resolver
argument_list|,
literal|null
argument_list|,
name|Context
operator|.
name|FULL_DUMP
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|tests
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|values
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|values
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|addNamespace
argument_list|(
literal|"A"
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|actual
init|=
name|evaluator
operator|.
name|evaluate
argument_list|(
literal|"A.key"
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
