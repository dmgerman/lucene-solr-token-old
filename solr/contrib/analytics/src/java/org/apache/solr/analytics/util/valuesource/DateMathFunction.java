begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analytics.util.valuesource
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analytics
operator|.
name|util
operator|.
name|valuesource
package|;
end_package
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
name|util
operator|.
name|Date
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
name|queries
operator|.
name|function
operator|.
name|FunctionValues
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|BytesRefFieldSource
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
name|analytics
operator|.
name|util
operator|.
name|AnalyticsParams
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
begin_comment
comment|/**  *<code>DateMathFunction</code> returns a start date modified by a list of DateMath operations.  */
end_comment
begin_class
DECL|class|DateMathFunction
specifier|public
class|class
name|DateMathFunction
extends|extends
name|MultiDateFunction
block|{
DECL|field|NAME
specifier|public
specifier|final
specifier|static
name|String
name|NAME
init|=
name|AnalyticsParams
operator|.
name|DATE_MATH
decl_stmt|;
DECL|field|parser
specifier|final
specifier|private
name|DateMathParser
name|parser
decl_stmt|;
comment|/**    * @param sources A list of ValueSource objects. The first element in the list    * should be a {@link DateFieldSource} or {@link ConstDateSource} object which    * represents the starting date. The rest of the field should be {@link BytesRefFieldSource}    * or {@link ConstStringSource} objects which contain the DateMath operations to perform on     * the start date.    */
DECL|method|DateMathFunction
specifier|public
name|DateMathFunction
parameter_list|(
name|ValueSource
index|[]
name|sources
parameter_list|)
block|{
name|super
argument_list|(
name|sources
argument_list|)
expr_stmt|;
name|parser
operator|=
operator|new
name|DateMathParser
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|protected
name|String
name|name
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|func
specifier|protected
name|long
name|func
parameter_list|(
name|int
name|doc
parameter_list|,
name|FunctionValues
index|[]
name|valsArr
parameter_list|)
block|{
name|long
name|time
init|=
literal|0
decl_stmt|;
name|Date
name|date
init|=
operator|(
name|Date
operator|)
name|valsArr
index|[
literal|0
index|]
operator|.
name|objectVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|setNow
argument_list|(
name|date
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|count
init|=
literal|1
init|;
name|count
operator|<
name|valsArr
operator|.
name|length
condition|;
name|count
operator|++
control|)
block|{
name|date
operator|=
name|parser
operator|.
name|parseMath
argument_list|(
name|valsArr
index|[
name|count
index|]
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setNow
argument_list|(
name|date
argument_list|)
expr_stmt|;
block|}
name|time
operator|=
name|parser
operator|.
name|getNow
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|time
operator|=
name|date
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
return|return
name|time
return|;
block|}
block|}
end_class
end_unit