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
name|builders
operator|.
name|QueryTreeBuilder
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
name|standard
operator|.
name|parser
operator|.
name|EscapeQuerySyntaxImpl
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
name|BooleanClause
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
name|BooleanQuery
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
name|Query
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
name|BooleanQuery
operator|.
name|TooManyClauses
import|;
end_import
begin_comment
comment|/**  * Builds a {@link BooleanQuery} object from a {@link BooleanQueryNode} object.  * Every children in the {@link BooleanQueryNode} object must be already tagged  * using {@link QueryTreeBuilder#QUERY_TREE_BUILDER_TAGID} with a {@link Query}  * object.<br>  *<br>  * It takes in consideration if the children is a {@link ModifierQueryNode} to  * define the {@link BooleanClause}.  */
end_comment
begin_class
DECL|class|BooleanQueryNodeBuilder
specifier|public
class|class
name|BooleanQueryNodeBuilder
implements|implements
name|StandardQueryBuilder
block|{
DECL|method|BooleanQueryNodeBuilder
specifier|public
name|BooleanQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|BooleanQuery
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|BooleanQueryNode
name|booleanNode
init|=
operator|(
name|BooleanQueryNode
operator|)
name|queryNode
decl_stmt|;
name|BooleanQuery
operator|.
name|Builder
name|bQuery
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|booleanNode
operator|.
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
name|children
control|)
block|{
name|Object
name|obj
init|=
name|child
operator|.
name|getTag
argument_list|(
name|QueryTreeBuilder
operator|.
name|QUERY_TREE_BUILDER_TAGID
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|!=
literal|null
condition|)
block|{
name|Query
name|query
init|=
operator|(
name|Query
operator|)
name|obj
decl_stmt|;
try|try
block|{
name|bQuery
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|getModifierValue
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TooManyClauses
name|ex
parameter_list|)
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
name|TOO_MANY_BOOLEAN_CLAUSES
argument_list|,
name|BooleanQuery
operator|.
name|getMaxClauseCount
argument_list|()
argument_list|,
name|queryNode
operator|.
name|toQueryString
argument_list|(
operator|new
name|EscapeQuerySyntaxImpl
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
name|bQuery
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|getModifierValue
specifier|private
specifier|static
name|BooleanClause
operator|.
name|Occur
name|getModifierValue
parameter_list|(
name|QueryNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|ModifierQueryNode
condition|)
block|{
name|ModifierQueryNode
name|mNode
init|=
operator|(
operator|(
name|ModifierQueryNode
operator|)
name|node
operator|)
decl_stmt|;
switch|switch
condition|(
name|mNode
operator|.
name|getModifier
argument_list|()
condition|)
block|{
case|case
name|MOD_REQ
case|:
return|return
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
return|;
case|case
name|MOD_NOT
case|:
return|return
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST_NOT
return|;
case|case
name|MOD_NONE
case|:
return|return
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
return|;
block|}
block|}
return|return
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
return|;
block|}
block|}
end_class
end_unit
