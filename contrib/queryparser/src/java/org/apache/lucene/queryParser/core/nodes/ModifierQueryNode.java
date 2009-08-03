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
name|core
operator|.
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import
begin_comment
comment|/**  * A {@link ModifierQueryNode} indicates the modifier value (+,-,?,NONE) for  * each term on the query string for example "+t1 -t2 t3" will have a tree of  *<BooleanQueryNode><ModifierQueryNode modifier="MOD_REQ"><t1/>  *</ModifierQueryNode><ModifierQueryNode modifier="MOD_NOT"><t2/>  *</ModifierQueryNode><t3/></BooleanQueryNode>  *   */
end_comment
begin_class
DECL|class|ModifierQueryNode
specifier|public
class|class
name|ModifierQueryNode
extends|extends
name|QueryNodeImpl
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|391209837953928169L
decl_stmt|;
DECL|enum|Modifier
specifier|public
enum|enum
name|Modifier
block|{
DECL|enum constant|MOD_NONE
DECL|enum constant|MOD_NOT
DECL|enum constant|MOD_REQ
name|MOD_NONE
block|,
name|MOD_NOT
block|,
name|MOD_REQ
block|;
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|MOD_NONE
case|:
return|return
literal|"MOD_NONE"
return|;
case|case
name|MOD_NOT
case|:
return|return
literal|"MOD_NOT"
return|;
case|case
name|MOD_REQ
case|:
return|return
literal|"MOD_REQ"
return|;
block|}
comment|// this code is never executed
return|return
literal|"MOD_DEFAULT"
return|;
block|}
DECL|method|toDigitString
specifier|public
name|String
name|toDigitString
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|MOD_NONE
case|:
return|return
literal|""
return|;
case|case
name|MOD_NOT
case|:
return|return
literal|"-"
return|;
case|case
name|MOD_REQ
case|:
return|return
literal|"+"
return|;
block|}
comment|// this code is never executed
return|return
literal|""
return|;
block|}
DECL|method|toLargeString
specifier|public
name|String
name|toLargeString
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|MOD_NONE
case|:
return|return
literal|""
return|;
case|case
name|MOD_NOT
case|:
return|return
literal|"NOT "
return|;
case|case
name|MOD_REQ
case|:
return|return
literal|"+"
return|;
block|}
comment|// this code is never executed
return|return
literal|""
return|;
block|}
block|}
DECL|field|modifier
specifier|private
name|Modifier
name|modifier
init|=
name|Modifier
operator|.
name|MOD_NONE
decl_stmt|;
comment|/**    * Used to store the modifier value on the original query string    *     * @param query    *          - QueryNode subtree    * @param mod    *          - Modifier Value    */
DECL|method|ModifierQueryNode
specifier|public
name|ModifierQueryNode
parameter_list|(
name|QueryNode
name|query
parameter_list|,
name|Modifier
name|mod
parameter_list|)
block|{
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryNodeError
argument_list|(
operator|new
name|MessageImpl
argument_list|(
name|QueryParserMessages
operator|.
name|PARAMETER_VALUE_NOT_SUPPORTED
argument_list|,
literal|"query"
argument_list|,
literal|"null"
argument_list|)
argument_list|)
throw|;
block|}
name|allocate
argument_list|()
expr_stmt|;
name|setLeaf
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|modifier
operator|=
name|mod
expr_stmt|;
block|}
DECL|method|getChild
specifier|public
name|QueryNode
name|getChild
parameter_list|()
block|{
return|return
name|getChildren
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|getModifier
specifier|public
name|Modifier
name|getModifier
parameter_list|()
block|{
return|return
name|this
operator|.
name|modifier
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<modifier operation='"
operator|+
name|this
operator|.
name|modifier
operator|.
name|toString
argument_list|()
operator|+
literal|"'>"
operator|+
literal|"\n"
operator|+
name|getChild
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"\n</modifier>"
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
if|if
condition|(
name|getChild
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|""
return|;
name|String
name|leftParenthensis
init|=
literal|""
decl_stmt|;
name|String
name|rightParenthensis
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|getChild
argument_list|()
operator|!=
literal|null
operator|&&
name|getChild
argument_list|()
operator|instanceof
name|ModifierQueryNode
condition|)
block|{
name|leftParenthensis
operator|=
literal|"("
expr_stmt|;
name|rightParenthensis
operator|=
literal|")"
expr_stmt|;
block|}
if|if
condition|(
name|getChild
argument_list|()
operator|instanceof
name|BooleanQueryNode
condition|)
block|{
return|return
name|this
operator|.
name|modifier
operator|.
name|toLargeString
argument_list|()
operator|+
name|leftParenthensis
operator|+
name|getChild
argument_list|()
operator|.
name|toQueryString
argument_list|(
name|escapeSyntaxParser
argument_list|)
operator|+
name|rightParenthensis
return|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|modifier
operator|.
name|toDigitString
argument_list|()
operator|+
name|leftParenthensis
operator|+
name|getChild
argument_list|()
operator|.
name|toQueryString
argument_list|(
name|escapeSyntaxParser
argument_list|)
operator|+
name|rightParenthensis
return|;
block|}
block|}
DECL|method|cloneTree
specifier|public
name|QueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|ModifierQueryNode
name|clone
init|=
operator|(
name|ModifierQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
name|clone
operator|.
name|modifier
operator|=
name|this
operator|.
name|modifier
expr_stmt|;
return|return
name|clone
return|;
block|}
comment|/**    * @param child    */
DECL|method|setChild
specifier|public
name|void
name|setChild
parameter_list|(
name|QueryNode
name|child
parameter_list|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|QueryNode
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|set
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
