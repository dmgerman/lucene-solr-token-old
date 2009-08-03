begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.core.util
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
name|util
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|List
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
name|QueryNodeError
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
name|nodes
operator|.
name|AndQueryNode
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
name|nodes
operator|.
name|QueryNode
import|;
end_import
begin_comment
comment|/**  * Allow joining 2 QueryNode Trees, into one.  */
end_comment
begin_class
DECL|class|QueryNodeOperation
specifier|public
specifier|final
class|class
name|QueryNodeOperation
block|{
DECL|method|QueryNodeOperation
specifier|private
name|QueryNodeOperation
parameter_list|()
block|{
comment|// Exists only to defeat instantiation.
block|}
DECL|enum|ANDOperation
specifier|private
enum|enum
name|ANDOperation
block|{
DECL|enum constant|BOTH
DECL|enum constant|Q1
DECL|enum constant|Q2
DECL|enum constant|NONE
name|BOTH
block|,
name|Q1
block|,
name|Q2
block|,
name|NONE
block|}
comment|/**    * perform a logical and of 2 QueryNode trees. if q1 and q2 are ANDQueryNode    * nodes it uses head Node from q1 and adds the children of q2 to q1 if q1 is    * a AND node and q2 is not, add q2 as a child of the head node of q1 if q2 is    * a AND node and q1 is not, add q1 as a child of the head node of q2 if q1    * and q2 are not ANDQueryNode nodes, create a AND node and make q1 and q2    * children of that node if q1 or q2 is null it returns the not null node if    * q1 = q2 = null it returns null    */
DECL|method|logicalAnd
specifier|public
specifier|final
specifier|static
name|QueryNode
name|logicalAnd
parameter_list|(
name|QueryNode
name|q1
parameter_list|,
name|QueryNode
name|q2
parameter_list|)
block|{
if|if
condition|(
name|q1
operator|==
literal|null
condition|)
return|return
name|q2
return|;
if|if
condition|(
name|q2
operator|==
literal|null
condition|)
return|return
name|q1
return|;
name|ANDOperation
name|op
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|q1
operator|instanceof
name|AndQueryNode
operator|&&
name|q2
operator|instanceof
name|AndQueryNode
condition|)
name|op
operator|=
name|ANDOperation
operator|.
name|BOTH
expr_stmt|;
elseif|else
if|if
condition|(
name|q1
operator|instanceof
name|AndQueryNode
condition|)
name|op
operator|=
name|ANDOperation
operator|.
name|Q1
expr_stmt|;
elseif|else
if|if
condition|(
name|q1
operator|instanceof
name|AndQueryNode
condition|)
name|op
operator|=
name|ANDOperation
operator|.
name|Q2
expr_stmt|;
else|else
name|op
operator|=
name|ANDOperation
operator|.
name|NONE
expr_stmt|;
try|try
block|{
name|QueryNode
name|result
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|op
condition|)
block|{
case|case
name|NONE
case|:
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryNode
argument_list|>
argument_list|()
decl_stmt|;
name|children
operator|.
name|add
argument_list|(
name|q1
operator|.
name|cloneTree
argument_list|()
argument_list|)
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
name|q2
operator|.
name|cloneTree
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|AndQueryNode
argument_list|(
name|children
argument_list|)
expr_stmt|;
return|return
name|result
return|;
case|case
name|Q1
case|:
name|result
operator|=
name|q1
operator|.
name|cloneTree
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|q2
operator|.
name|cloneTree
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
case|case
name|Q2
case|:
name|result
operator|=
name|q2
operator|.
name|cloneTree
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|q1
operator|.
name|cloneTree
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
case|case
name|BOTH
case|:
name|result
operator|=
name|q1
operator|.
name|cloneTree
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|q2
operator|.
name|cloneTree
argument_list|()
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
catch|catch
parameter_list|(
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryNodeError
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class
end_unit
