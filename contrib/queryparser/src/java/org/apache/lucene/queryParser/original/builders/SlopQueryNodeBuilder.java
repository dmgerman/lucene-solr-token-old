begin_unit
begin_package
DECL|package|org.apache.lucene.queryParser.original.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|original
operator|.
name|builders
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
name|nodes
operator|.
name|SlopQueryNode
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
name|MultiPhraseQuery
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
name|PhraseQuery
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
begin_comment
comment|/**  * This builder basically reads the {@link Query} object set on the  * {@link SlopQueryNode} child using  * {@link QueryTreeBuilder#QUERY_TREE_BUILDER_TAGID} and applies the slop value  * defined in the {@link SlopQueryNode}.  */
end_comment
begin_class
DECL|class|SlopQueryNodeBuilder
specifier|public
class|class
name|SlopQueryNodeBuilder
implements|implements
name|OriginalQueryBuilder
block|{
DECL|method|SlopQueryNodeBuilder
specifier|public
name|SlopQueryNodeBuilder
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|build
specifier|public
name|Query
name|build
parameter_list|(
name|QueryNode
name|queryNode
parameter_list|)
throws|throws
name|QueryNodeException
block|{
name|SlopQueryNode
name|phraseSlopNode
init|=
operator|(
name|SlopQueryNode
operator|)
name|queryNode
decl_stmt|;
name|Query
name|query
init|=
operator|(
name|Query
operator|)
name|phraseSlopNode
operator|.
name|getChild
argument_list|()
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
name|query
operator|instanceof
name|PhraseQuery
condition|)
block|{
operator|(
operator|(
name|PhraseQuery
operator|)
name|query
operator|)
operator|.
name|setSlop
argument_list|(
name|phraseSlopNode
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|MultiPhraseQuery
operator|)
name|query
operator|)
operator|.
name|setSlop
argument_list|(
name|phraseSlopNode
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
block|}
end_class
end_unit
