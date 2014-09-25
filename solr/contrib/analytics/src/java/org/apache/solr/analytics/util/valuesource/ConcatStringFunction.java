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
name|solr
operator|.
name|analytics
operator|.
name|util
operator|.
name|AnalyticsParams
import|;
end_import
begin_comment
comment|/**  *<code>ConcatStringFunction</code> concatenates the string values of its   * components in the order given.  */
end_comment
begin_class
DECL|class|ConcatStringFunction
specifier|public
class|class
name|ConcatStringFunction
extends|extends
name|MultiStringFunction
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
name|CONCATENATE
decl_stmt|;
DECL|method|ConcatStringFunction
specifier|public
name|ConcatStringFunction
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
block|}
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
name|String
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|FunctionValues
name|val
range|:
name|valsArr
control|)
block|{
name|String
name|v
init|=
name|val
operator|.
name|strVal
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|v
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
block|}
end_class
end_unit