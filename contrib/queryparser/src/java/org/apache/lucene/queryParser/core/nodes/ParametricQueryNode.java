begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.core.nodes
package|package
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
name|nodes
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import
begin_comment
comment|/**  * A {@link ParametricQueryNode} represents LE, LT, GE, GT, EQ, NE query.  * Example: date>= "2009-10-10" OR price = 200  */
end_comment
begin_class
DECL|class|ParametricQueryNode
specifier|public
class|class
name|ParametricQueryNode
extends|extends
name|FieldQueryNode
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|5770038129741218116L
decl_stmt|;
DECL|field|operator
specifier|private
name|CompareOperator
name|operator
decl_stmt|;
DECL|enum|CompareOperator
specifier|public
enum|enum
name|CompareOperator
block|{
DECL|enum constant|LE
DECL|enum constant|LT
DECL|enum constant|GE
DECL|enum constant|GT
DECL|enum constant|EQ
DECL|enum constant|NE
name|LE
block|,
name|LT
block|,
name|GE
block|,
name|GT
block|,
name|EQ
block|,
name|NE
block|;
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|LE
operator|.
name|equals
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
literal|"<="
return|;
block|}
elseif|else
if|if
condition|(
name|LT
operator|.
name|equals
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
literal|"<"
return|;
block|}
elseif|else
if|if
condition|(
name|GE
operator|.
name|equals
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
literal|">="
return|;
block|}
elseif|else
if|if
condition|(
name|GT
operator|.
name|equals
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
literal|">"
return|;
block|}
elseif|else
if|if
condition|(
name|EQ
operator|.
name|equals
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
literal|"="
return|;
block|}
elseif|else
if|if
condition|(
name|NE
operator|.
name|equals
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
literal|"!="
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown operator"
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * @param field    *          - field name    * @param comp    *          - CompareOperator    * @param value    *          - text value    * @param begin    *          - position in the query string    * @param end    *          - position in the query string    */
DECL|method|ParametricQueryNode
specifier|public
name|ParametricQueryNode
parameter_list|(
name|CharSequence
name|field
parameter_list|,
name|CompareOperator
name|comp
parameter_list|,
name|CharSequence
name|value
parameter_list|,
name|int
name|begin
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|super
argument_list|(
name|field
argument_list|,
name|value
argument_list|,
name|begin
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|comp
expr_stmt|;
name|setLeaf
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|getOperand
specifier|public
name|CharSequence
name|getOperand
parameter_list|()
block|{
return|return
name|getText
argument_list|()
return|;
block|}
DECL|method|toQueryString
specifier|public
name|CharSequence
name|toQueryString
parameter_list|(
name|EscapeQuerySyntax
name|escapeSyntaxParser
parameter_list|)
block|{
return|return
name|this
operator|.
name|field
operator|+
literal|""
operator|+
name|this
operator|.
name|operator
operator|.
name|toString
argument_list|()
operator|+
literal|"\""
operator|+
name|this
operator|.
name|text
operator|+
literal|"\""
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<parametric field='"
operator|+
name|this
operator|.
name|field
operator|+
literal|"' operator='"
operator|+
name|this
operator|.
name|operator
operator|.
name|toString
argument_list|()
operator|+
literal|"' text='"
operator|+
name|this
operator|.
name|text
operator|+
literal|"'/>"
return|;
block|}
DECL|method|cloneTree
specifier|public
name|ParametricQueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|ParametricQueryNode
name|clone
init|=
operator|(
name|ParametricQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
name|clone
operator|.
name|operator
operator|=
name|this
operator|.
name|operator
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/**    * @return the operator    */
DECL|method|getOperator
specifier|public
name|CompareOperator
name|getOperator
parameter_list|()
block|{
return|return
name|this
operator|.
name|operator
return|;
block|}
block|}
end_class
end_unit
