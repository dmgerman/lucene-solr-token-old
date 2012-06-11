begin_unit
begin_package
DECL|package|org.apache.lucene.queryparser.flexible.standard.processors
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
name|processors
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|config
operator|.
name|QueryConfigHandler
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
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|nodes
operator|.
name|BooleanQueryNode
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
name|GroupQueryNode
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
name|ModifierQueryNode
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
name|OrQueryNode
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
name|nodes
operator|.
name|ModifierQueryNode
operator|.
name|Modifier
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
name|parser
operator|.
name|SyntaxParser
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
name|processors
operator|.
name|QueryNodeProcessor
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
name|StandardQueryConfigHandler
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
name|StandardQueryConfigHandler
operator|.
name|ConfigurationKeys
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
name|StandardQueryConfigHandler
operator|.
name|Operator
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
name|BooleanModifierNode
import|;
end_import
begin_comment
comment|/**  * The {@link SyntaxParser}  * generates query node trees that consider the boolean operator precedence, but  * Lucene current syntax does not support boolean precedence, so this processor  * remove all the precedence and apply the equivalent modifier according to the  * boolean operation defined on an specific query node.<br/>  *<br/>  * If there is a {@link GroupQueryNode} in the query node tree, the query node  * tree is not merged with the one above it.  *   * Example: TODO: describe a good example to show how this processor works  *   * @see org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler  */
end_comment
begin_class
DECL|class|GroupQueryNodeProcessor
specifier|public
class|class
name|GroupQueryNodeProcessor
implements|implements
name|QueryNodeProcessor
block|{
DECL|field|queryNodeList
specifier|private
name|ArrayList
argument_list|<
name|QueryNode
argument_list|>
name|queryNodeList
decl_stmt|;
DECL|field|latestNodeVerified
specifier|private
name|boolean
name|latestNodeVerified
decl_stmt|;
DECL|field|queryConfig
specifier|private
name|QueryConfigHandler
name|queryConfig
decl_stmt|;
DECL|field|usingAnd
specifier|private
name|Boolean
name|usingAnd
init|=
literal|false
decl_stmt|;
DECL|method|GroupQueryNodeProcessor
specifier|public
name|GroupQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|process
specifier|public
name|QueryNode
name|process
parameter_list|(
name|QueryNode
name|queryTree
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|Operator
name|defaultOperator
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|get
argument_list|(
name|ConfigurationKeys
operator|.
name|DEFAULT_OPERATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultOperator
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"DEFAULT_OPERATOR should be set on the QueryConfigHandler"
argument_list|)
throw|;
block|}
name|this
operator|.
name|usingAnd
operator|=
name|StandardQueryConfigHandler
operator|.
name|Operator
operator|.
name|AND
operator|==
name|defaultOperator
expr_stmt|;
if|if
condition|(
name|queryTree
operator|instanceof
name|GroupQueryNode
condition|)
block|{
name|queryTree
operator|=
operator|(
operator|(
name|GroupQueryNode
operator|)
name|queryTree
operator|)
operator|.
name|getChild
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|queryNodeList
operator|=
operator|new
name|ArrayList
argument_list|<
name|QueryNode
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|latestNodeVerified
operator|=
literal|false
expr_stmt|;
name|readTree
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|QueryNode
argument_list|>
name|actualQueryNodeList
init|=
name|this
operator|.
name|queryNodeList
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
name|actualQueryNodeList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|QueryNode
name|node
init|=
name|actualQueryNodeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|GroupQueryNode
condition|)
block|{
name|actualQueryNodeList
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|process
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|usingAnd
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|queryTree
operator|instanceof
name|BooleanQueryNode
condition|)
block|{
name|queryTree
operator|.
name|set
argument_list|(
name|actualQueryNodeList
argument_list|)
expr_stmt|;
return|return
name|queryTree
return|;
block|}
else|else
block|{
return|return
operator|new
name|BooleanQueryNode
argument_list|(
name|actualQueryNodeList
argument_list|)
return|;
block|}
block|}
comment|/**    */
DECL|method|applyModifier
specifier|private
name|QueryNode
name|applyModifier
parameter_list|(
name|QueryNode
name|node
parameter_list|,
name|QueryNode
name|parent
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|usingAnd
condition|)
block|{
if|if
condition|(
name|parent
operator|instanceof
name|OrQueryNode
condition|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|ModifierQueryNode
condition|)
block|{
name|ModifierQueryNode
name|modNode
init|=
operator|(
name|ModifierQueryNode
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|modNode
operator|.
name|getModifier
argument_list|()
operator|==
name|Modifier
operator|.
name|MOD_REQ
condition|)
block|{
return|return
name|modNode
operator|.
name|getChild
argument_list|()
return|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|node
operator|instanceof
name|ModifierQueryNode
condition|)
block|{
name|ModifierQueryNode
name|modNode
init|=
operator|(
name|ModifierQueryNode
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|modNode
operator|.
name|getModifier
argument_list|()
operator|==
name|Modifier
operator|.
name|MOD_NONE
condition|)
block|{
return|return
operator|new
name|BooleanModifierNode
argument_list|(
name|modNode
operator|.
name|getChild
argument_list|()
argument_list|,
name|Modifier
operator|.
name|MOD_REQ
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|BooleanModifierNode
argument_list|(
name|node
argument_list|,
name|Modifier
operator|.
name|MOD_REQ
argument_list|)
return|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|node
operator|.
name|getParent
argument_list|()
operator|instanceof
name|AndQueryNode
condition|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|ModifierQueryNode
condition|)
block|{
name|ModifierQueryNode
name|modNode
init|=
operator|(
name|ModifierQueryNode
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|modNode
operator|.
name|getModifier
argument_list|()
operator|==
name|Modifier
operator|.
name|MOD_NONE
condition|)
block|{
return|return
operator|new
name|BooleanModifierNode
argument_list|(
name|modNode
operator|.
name|getChild
argument_list|()
argument_list|,
name|Modifier
operator|.
name|MOD_REQ
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|BooleanModifierNode
argument_list|(
name|node
argument_list|,
name|Modifier
operator|.
name|MOD_REQ
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|node
return|;
block|}
DECL|method|readTree
specifier|private
name|void
name|readTree
parameter_list|(
name|QueryNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|BooleanQueryNode
condition|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
operator|&&
name|children
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|readTree
argument_list|(
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|processNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|readTree
argument_list|(
name|children
operator|.
name|get
argument_list|(
name|children
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|processNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|processNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|processNode
specifier|private
name|void
name|processNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|AndQueryNode
operator|||
name|node
operator|instanceof
name|OrQueryNode
condition|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|latestNodeVerified
operator|&&
operator|!
name|this
operator|.
name|queryNodeList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|queryNodeList
operator|.
name|add
argument_list|(
name|applyModifier
argument_list|(
name|this
operator|.
name|queryNodeList
operator|.
name|remove
argument_list|(
name|this
operator|.
name|queryNodeList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|latestNodeVerified
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|BooleanQueryNode
operator|)
condition|)
block|{
name|this
operator|.
name|queryNodeList
operator|.
name|add
argument_list|(
name|applyModifier
argument_list|(
name|node
argument_list|,
name|node
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|latestNodeVerified
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|getQueryConfigHandler
specifier|public
name|QueryConfigHandler
name|getQueryConfigHandler
parameter_list|()
block|{
return|return
name|this
operator|.
name|queryConfig
return|;
block|}
DECL|method|setQueryConfigHandler
specifier|public
name|void
name|setQueryConfigHandler
parameter_list|(
name|QueryConfigHandler
name|queryConfigHandler
parameter_list|)
block|{
name|this
operator|.
name|queryConfig
operator|=
name|queryConfigHandler
expr_stmt|;
block|}
block|}
end_class
end_unit
