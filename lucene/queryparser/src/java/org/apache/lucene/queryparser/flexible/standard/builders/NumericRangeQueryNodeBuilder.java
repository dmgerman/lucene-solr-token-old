begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.builders
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
name|standard
operator|.
name|builders
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
name|lucene
operator|.
name|document
operator|.
name|FieldType
operator|.
name|NumericType
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
name|queryparser
operator|.
name|flexible
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
name|queryparser
operator|.
name|flexible
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|util
operator|.
name|StringUtils
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
name|queryparser
operator|.
name|flexible
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|config
operator|.
name|NumericConfig
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|nodes
operator|.
name|NumericQueryNode
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|nodes
operator|.
name|NumericRangeQueryNode
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
name|search
operator|.
name|NumericRangeQuery
import|;
end_import
begin_comment
comment|/**  * Builds {@link NumericRangeQuery}s out of {@link NumericRangeQueryNode}s.  *  * @see NumericRangeQuery  * @see NumericRangeQueryNode  */
end_comment
begin_class
DECL|class|NumericRangeQueryNodeBuilder
specifier|public
class|class
name|NumericRangeQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
comment|/**    * Constructs a {@link NumericRangeQueryNodeBuilder} object.    */
DECL|method|NumericRangeQueryNodeBuilder
specifier|public
name|NumericRangeQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|build
specifier|public
name|NumericRangeQuery
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|NumericRangeQueryNode
name|numericRangeNode
init|=
operator|(
name|NumericRangeQueryNode
operator|)
name|queryNode
decl_stmt|;
name|NumericQueryNode
name|lowerNumericNode
init|=
name|numericRangeNode
operator|.
name|getLowerBound
argument_list|()
decl_stmt|;
name|NumericQueryNode
name|upperNumericNode
init|=
name|numericRangeNode
operator|.
name|getUpperBound
argument_list|()
decl_stmt|;
name|Number
name|lowerNumber
init|=
name|lowerNumericNode
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Number
name|upperNumber
init|=
name|upperNumericNode
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|NumericConfig
name|numericConfig
init|=
name|numericRangeNode
operator|.
name|getNumericConfig
argument_list|()
decl_stmt|;
name|NumericType
name|numberType
init|=
name|numericConfig
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|field
init|=
name|StringUtils
operator|.
name|toString
argument_list|(
name|numericRangeNode
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|minInclusive
init|=
name|numericRangeNode
operator|.
name|isLowerInclusive
argument_list|()
decl_stmt|;
name|boolean
name|maxInclusive
init|=
name|numericRangeNode
operator|.
name|isUpperInclusive
argument_list|()
decl_stmt|;
name|int
name|precisionStep
init|=
name|numericConfig
operator|.
name|getPrecisionStep
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|numberType
condition|)
block|{
case|case
name|LONG
case|:
return|return
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
operator|(
name|Long
operator|)
name|lowerNumber
argument_list|,
operator|(
name|Long
operator|)
name|upperNumber
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
case|case
name|INT
case|:
return|return
name|NumericRangeQuery
operator|.
name|newIntRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
operator|(
name|Integer
operator|)
name|lowerNumber
argument_list|,
operator|(
name|Integer
operator|)
name|upperNumber
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
case|case
name|FLOAT
case|:
return|return
name|NumericRangeQuery
operator|.
name|newFloatRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
operator|(
name|Float
operator|)
name|lowerNumber
argument_list|,
operator|(
name|Float
operator|)
name|upperNumber
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
case|case
name|DOUBLE
case|:
return|return
name|NumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
name|field
argument_list|,
name|precisionStep
argument_list|,
operator|(
name|Double
operator|)
name|lowerNumber
argument_list|,
operator|(
name|Double
operator|)
name|upperNumber
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|)
return|;
default|default :
throw|throw
operator|new
name|QueryNodeException
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|UNSUPPORTED_NUMERIC_DATA_TYPE
argument_list|,
name|numberType
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
