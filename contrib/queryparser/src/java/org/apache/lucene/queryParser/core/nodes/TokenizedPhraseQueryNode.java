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
name|parser
operator|.
name|EscapeQuerySyntax
import|;
end_import
begin_comment
comment|/**  * A {@link TokenizedPhraseQueryNode} represents a node created by a code that  * tokenizes/lemmatizes/analizes.  */
end_comment
begin_class
DECL|class|TokenizedPhraseQueryNode
specifier|public
class|class
name|TokenizedPhraseQueryNode
extends|extends
name|QueryNodeImpl
implements|implements
name|FieldableNode
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|7185108320787917541L
decl_stmt|;
DECL|method|TokenizedPhraseQueryNode
specifier|public
name|TokenizedPhraseQueryNode
parameter_list|()
block|{
name|setLeaf
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|allocate
argument_list|()
expr_stmt|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|getChildren
argument_list|()
operator|==
literal|null
operator|||
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|"<tokenizedphrase/>"
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<tokenizedtphrase>"
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryNode
name|child
range|:
name|getChildren
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|child
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n</tokenizedphrase>"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// This text representation is not re-parseable
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
name|getChildren
argument_list|()
operator|==
literal|null
operator|||
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|""
return|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|filler
init|=
literal|""
decl_stmt|;
for|for
control|(
name|QueryNode
name|child
range|:
name|getChildren
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|filler
argument_list|)
operator|.
name|append
argument_list|(
name|child
operator|.
name|toQueryString
argument_list|(
name|escapeSyntaxParser
argument_list|)
argument_list|)
expr_stmt|;
name|filler
operator|=
literal|","
expr_stmt|;
block|}
return|return
literal|"[TP["
operator|+
name|sb
operator|.
name|toString
argument_list|()
operator|+
literal|"]]"
return|;
block|}
DECL|method|cloneTree
specifier|public
name|QueryNode
name|cloneTree
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|TokenizedPhraseQueryNode
name|clone
init|=
operator|(
name|TokenizedPhraseQueryNode
operator|)
name|super
operator|.
name|cloneTree
argument_list|()
decl_stmt|;
comment|// nothing to do
return|return
name|clone
return|;
block|}
DECL|method|getField
specifier|public
name|CharSequence
name|getField
parameter_list|()
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
operator|||
name|children
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|(
operator|(
name|FieldableNode
operator|)
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getField
argument_list|()
return|;
block|}
block|}
DECL|method|setField
specifier|public
name|void
name|setField
parameter_list|(
name|CharSequence
name|fieldName
parameter_list|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|QueryNode
name|child
range|:
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|child
operator|instanceof
name|FieldableNode
condition|)
block|{
operator|(
operator|(
name|FieldableNode
operator|)
name|child
operator|)
operator|.
name|setField
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class
begin_comment
comment|// end class MultitermQueryNode
end_comment
end_unit
