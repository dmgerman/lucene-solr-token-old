begin_unit
begin_package
DECL|package|org.apache.lucene.expressions
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|expressions
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
name|docvalues
operator|.
name|DoubleDocValues
import|;
end_import
begin_comment
comment|/** A {@link FunctionValues} which evaluates an expression */
end_comment
begin_class
DECL|class|ExpressionFunctionValues
class|class
name|ExpressionFunctionValues
extends|extends
name|DoubleDocValues
block|{
DECL|field|expression
specifier|final
name|Expression
name|expression
decl_stmt|;
DECL|field|functionValues
specifier|final
name|FunctionValues
index|[]
name|functionValues
decl_stmt|;
DECL|field|currentDocument
name|int
name|currentDocument
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|currentValue
name|double
name|currentValue
decl_stmt|;
DECL|method|ExpressionFunctionValues
name|ExpressionFunctionValues
parameter_list|(
name|ValueSource
name|parent
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|FunctionValues
index|[]
name|functionValues
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
if|if
condition|(
name|expression
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
if|if
condition|(
name|functionValues
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|functionValues
operator|=
name|functionValues
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doubleVal
specifier|public
name|double
name|doubleVal
parameter_list|(
name|int
name|document
parameter_list|)
block|{
if|if
condition|(
name|currentDocument
operator|!=
name|document
condition|)
block|{
name|currentDocument
operator|=
name|document
expr_stmt|;
name|currentValue
operator|=
name|expression
operator|.
name|evaluate
argument_list|(
name|document
argument_list|,
name|functionValues
argument_list|)
expr_stmt|;
block|}
return|return
name|currentValue
return|;
block|}
block|}
end_class
end_unit
