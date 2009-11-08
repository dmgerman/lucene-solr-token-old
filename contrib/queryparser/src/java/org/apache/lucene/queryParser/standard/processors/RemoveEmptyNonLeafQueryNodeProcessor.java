begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.standard.processors
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
name|processors
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
name|LinkedList
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|MatchNoDocsQueryNode
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
name|queryParser
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
name|queryParser
operator|.
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorImpl
import|;
end_import
begin_comment
comment|/**  * This processor removes every {@link QueryNode} that is not a leaf and has not  * children. If after processing the entire tree the root node is not a leaf and  * has no children, a {@link MatchNoDocsQueryNode} object is returned.<br/>  *<br/>  * This processor is used at the end of a pipeline to avoid invalid query node  * tree structures like a {@link GroupQueryNode} or {@link ModifierQueryNode}  * with no children.<br/>  *   * @see QueryNode  * @see MatchNoDocsQueryNode  */
end_comment
begin_class
DECL|class|RemoveEmptyNonLeafQueryNodeProcessor
specifier|public
class|class
name|RemoveEmptyNonLeafQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|field|childrenBuffer
specifier|private
name|LinkedList
argument_list|<
name|QueryNode
argument_list|>
name|childrenBuffer
init|=
operator|new
name|LinkedList
argument_list|<
name|QueryNode
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|RemoveEmptyNonLeafQueryNodeProcessor
specifier|public
name|RemoveEmptyNonLeafQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
annotation|@
name|Override
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
name|queryTree
operator|=
name|super
operator|.
name|process
argument_list|(
name|queryTree
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|queryTree
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
init|=
name|queryTree
operator|.
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
operator|new
name|MatchNoDocsQueryNode
argument_list|()
return|;
block|}
block|}
return|return
name|queryTree
return|;
block|}
annotation|@
name|Override
DECL|method|postProcessNode
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|preProcessNode
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|setChildrenOrder
specifier|protected
name|List
argument_list|<
name|QueryNode
argument_list|>
name|setChildrenOrder
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
throws|throws
name|QueryNodeException
block|{
try|try
block|{
for|for
control|(
name|QueryNode
name|child
range|:
name|children
control|)
block|{
if|if
condition|(
operator|!
name|child
operator|.
name|isLeaf
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|QueryNode
argument_list|>
name|grandChildren
init|=
name|child
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|grandChildren
operator|!=
literal|null
operator|&&
name|grandChildren
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|childrenBuffer
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|childrenBuffer
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
name|children
operator|.
name|clear
argument_list|()
expr_stmt|;
name|children
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|childrenBuffer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|this
operator|.
name|childrenBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|children
return|;
block|}
block|}
end_class
end_unit
