begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.io.stream.metrics
package|package
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
name|io
operator|.
name|stream
operator|.
name|metrics
package|;
end_package
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|io
operator|.
name|Tuple
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpression
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamExpressionParameter
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
name|io
operator|.
name|stream
operator|.
name|expr
operator|.
name|StreamFactory
import|;
end_import
begin_class
DECL|class|MeanMetric
specifier|public
class|class
name|MeanMetric
extends|extends
name|Metric
block|{
comment|// How'd the MeanMetric get to be so mean?
comment|// Maybe it was born with it.
comment|// Maybe it was mayba-mean.
comment|//
comment|// I'll see myself out.
DECL|field|columnName
specifier|private
name|String
name|columnName
decl_stmt|;
DECL|field|doubleSum
specifier|private
name|double
name|doubleSum
decl_stmt|;
DECL|field|longSum
specifier|private
name|long
name|longSum
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|method|MeanMetric
specifier|public
name|MeanMetric
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
name|init
argument_list|(
literal|"avg"
argument_list|,
name|columnName
argument_list|)
expr_stmt|;
block|}
DECL|method|MeanMetric
specifier|public
name|MeanMetric
parameter_list|(
name|StreamExpression
name|expression
parameter_list|,
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
comment|// grab all parameters out
name|String
name|functionName
init|=
name|expression
operator|.
name|getFunctionName
argument_list|()
decl_stmt|;
name|String
name|columnName
init|=
name|factory
operator|.
name|getValueOperand
argument_list|(
name|expression
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// validate expression contains only what we want.
if|if
condition|(
literal|null
operator|==
name|columnName
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid expression %s - expected %s(columnName)"
argument_list|,
name|expression
argument_list|,
name|functionName
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|1
operator|!=
name|expression
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Invalid expression %s - unknown operands found"
argument_list|,
name|expression
argument_list|)
argument_list|)
throw|;
block|}
name|init
argument_list|(
name|functionName
argument_list|,
name|columnName
argument_list|)
expr_stmt|;
block|}
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|functionName
parameter_list|,
name|String
name|columnName
parameter_list|)
block|{
name|this
operator|.
name|columnName
operator|=
name|columnName
expr_stmt|;
name|setFunctionName
argument_list|(
name|functionName
argument_list|)
expr_stmt|;
name|setIdentifier
argument_list|(
name|functionName
argument_list|,
literal|"("
argument_list|,
name|columnName
argument_list|,
literal|")"
argument_list|)
expr_stmt|;
block|}
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|Tuple
name|tuple
parameter_list|)
block|{
operator|++
name|count
expr_stmt|;
name|Object
name|o
init|=
name|tuple
operator|.
name|get
argument_list|(
name|columnName
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Double
condition|)
block|{
name|Double
name|d
init|=
operator|(
name|Double
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|columnName
argument_list|)
decl_stmt|;
name|doubleSum
operator|+=
name|d
expr_stmt|;
block|}
else|else
block|{
name|Long
name|l
init|=
operator|(
name|Long
operator|)
name|tuple
operator|.
name|get
argument_list|(
name|columnName
argument_list|)
decl_stmt|;
name|longSum
operator|+=
name|l
expr_stmt|;
block|}
block|}
DECL|method|newInstance
specifier|public
name|Metric
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|MeanMetric
argument_list|(
name|columnName
argument_list|)
return|;
block|}
DECL|method|getColumns
specifier|public
name|String
index|[]
name|getColumns
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|columnName
block|}
return|;
block|}
DECL|method|getValue
specifier|public
name|Double
name|getValue
parameter_list|()
block|{
name|double
name|dcount
init|=
operator|(
name|double
operator|)
name|count
decl_stmt|;
if|if
condition|(
name|longSum
operator|==
literal|0
condition|)
block|{
return|return
name|doubleSum
operator|/
name|dcount
return|;
block|}
else|else
block|{
return|return
name|longSum
operator|/
name|dcount
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toExpression
specifier|public
name|StreamExpressionParameter
name|toExpression
parameter_list|(
name|StreamFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|StreamExpression
argument_list|(
name|getFunctionName
argument_list|()
argument_list|)
operator|.
name|withParameter
argument_list|(
name|columnName
argument_list|)
return|;
block|}
block|}
end_class
end_unit
