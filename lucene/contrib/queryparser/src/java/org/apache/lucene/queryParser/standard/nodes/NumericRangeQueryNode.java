begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.standard.nodes
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|nodes
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
name|document
operator|.
name|NumericField
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
name|messages
operator|.
name|MessageImpl
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
name|queryParser
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryParser
operator|.
name|core
operator|.
name|messages
operator|.
name|QueryParserMessages
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|NumericConfig
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment
begin_class
DECL|class|NumericRangeQueryNode
specifier|public
class|class
name|NumericRangeQueryNode
extends|extends
name|AbstractRangeQueryNode
argument_list|<
name|NumericQueryNode
argument_list|>
block|{
DECL|field|numericConfig
specifier|public
name|NumericConfig
name|numericConfig
decl_stmt|;
DECL|method|NumericRangeQueryNode
specifier|public
name|NumericRangeQueryNode
parameter_list|(
name|NumericQueryNode
name|lower
parameter_list|,
name|NumericQueryNode
name|upper
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|,
name|NumericConfig
name|numericConfig
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|setBounds
argument_list|(
name|lower
argument_list|,
name|upper
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|,
name|numericConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumericDataType
specifier|private
specifier|static
name|NumericField
operator|.
name|DataType
name|getNumericDataType
parameter_list|(
name|Number
name|number
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|number
operator|instanceof
name|Long
condition|)
block|{
return|return
name|NumericField
operator|.
name|DataType
operator|.
name|LONG
return|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Integer
condition|)
block|{
return|return
name|NumericField
operator|.
name|DataType
operator|.
name|INT
return|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Double
condition|)
block|{
return|return
name|NumericField
operator|.
name|DataType
operator|.
name|DOUBLE
return|;
block|}
elseif|else
if|if
condition|(
name|number
operator|instanceof
name|Float
condition|)
block|{
return|return
name|NumericField
operator|.
name|DataType
operator|.
name|FLOAT
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryNodeException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|NUMBER_CLASS_NOT_SUPPORTED_BY_NUMERIC_RANGE_QUERY
argument_list|,
name|number
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|setBounds
specifier|public
name|void
name|setBounds
parameter_list|(
name|NumericQueryNode
name|lower
parameter_list|,
name|NumericQueryNode
name|upper
parameter_list|,
name|boolean
name|lowerInclusive
parameter_list|,
name|boolean
name|upperInclusive
parameter_list|,
name|NumericConfig
name|numericConfig
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|numericConfig
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numericConfig cannot be null!"
argument_list|)
throw|;
block|}
name|NumericField
operator|.
name|DataType
name|lowerNumberType
decl_stmt|,
name|upperNumberType
decl_stmt|;
if|if
condition|(
name|lower
operator|!=
literal|null
operator|&&
name|lower
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|lowerNumberType
operator|=
name|getNumericDataType
argument_list|(
name|lower
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lowerNumberType
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|upper
operator|!=
literal|null
operator|&&
name|upper
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|upperNumberType
operator|=
name|getNumericDataType
argument_list|(
name|upper
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|upperNumberType
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|lowerNumberType
operator|!=
literal|null
operator|&&
operator|!
name|lowerNumberType
operator|.
name|equals
argument_list|(
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"lower value's type should be the same as numericConfig type: "
operator|+
name|lowerNumberType
operator|+
literal|" != "
operator|+
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|upperNumberType
operator|!=
literal|null
operator|&&
operator|!
name|upperNumberType
operator|.
name|equals
argument_list|(
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"upper value's type should be the same as numericConfig type: "
operator|+
name|upperNumberType
operator|+
literal|" != "
operator|+
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
name|super
operator|.
name|setBounds
argument_list|(
name|lower
argument_list|,
name|upper
argument_list|,
name|lowerInclusive
argument_list|,
name|upperInclusive
argument_list|)
expr_stmt|;
name|this
operator|.
name|numericConfig
operator|=
name|numericConfig
expr_stmt|;
block|}
DECL|method|getNumericConfig
specifier|public
name|NumericConfig
name|getNumericConfig
parameter_list|()
block|{
return|return
name|this
operator|.
name|numericConfig
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"<numericRange lowerInclusive='"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|isLowerInclusive
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' upperInclusive='"
argument_list|)
operator|.
name|append
argument_list|(
name|isUpperInclusive
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' precisionStep='"
operator|+
name|numericConfig
operator|.
name|getPrecisionStep
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"' type='"
operator|+
name|numericConfig
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"'>\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getLowerBound
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getUpperBound
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</numericRange>"
argument_list|)
expr_stmt|;
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
