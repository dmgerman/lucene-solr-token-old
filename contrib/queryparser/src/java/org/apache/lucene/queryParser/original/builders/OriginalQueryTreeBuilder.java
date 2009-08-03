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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|BoostQueryNode
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
name|FieldQueryNode
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
name|FuzzyQueryNode
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
name|MatchAllDocsQueryNode
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
name|PrefixWildcardQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|TokenizedPhraseQueryNode
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
name|WildcardQueryNode
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
name|original
operator|.
name|nodes
operator|.
name|OriginalBooleanQueryNode
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
name|original
operator|.
name|nodes
operator|.
name|MultiPhraseQueryNode
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
name|original
operator|.
name|nodes
operator|.
name|RangeQueryNode
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
name|original
operator|.
name|processors
operator|.
name|OriginalQueryNodeProcessorPipeline
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
comment|/**  * This query tree builder only defines the necessary map to build a  * {@link Query} tree object. It should be used to generate a {@link Query} tree  * object from a query node tree processed by a  * {@link OriginalQueryNodeProcessorPipeline}.<br/>  *   * @see QueryTreeBuilder  * @see OriginalQueryNodeProcessorPipeline  */
end_comment
begin_class
DECL|class|OriginalQueryTreeBuilder
specifier|public
class|class
name|OriginalQueryTreeBuilder
extends|extends
name|QueryTreeBuilder
implements|implements
name|OriginalQueryBuilder
block|{
DECL|method|OriginalQueryTreeBuilder
specifier|public
name|OriginalQueryTreeBuilder
parameter_list|()
block|{
name|setBuilder
argument_list|(
name|GroupQueryNode
operator|.
name|class
argument_list|,
operator|new
name|GroupQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|FieldQueryNode
operator|.
name|class
argument_list|,
operator|new
name|FieldQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|BooleanQueryNode
operator|.
name|class
argument_list|,
operator|new
name|BooleanQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|FuzzyQueryNode
operator|.
name|class
argument_list|,
operator|new
name|FuzzyQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|BoostQueryNode
operator|.
name|class
argument_list|,
operator|new
name|BoostQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|ModifierQueryNode
operator|.
name|class
argument_list|,
operator|new
name|ModifierQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|WildcardQueryNode
operator|.
name|class
argument_list|,
operator|new
name|WildcardQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|TokenizedPhraseQueryNode
operator|.
name|class
argument_list|,
operator|new
name|PhraseQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|MatchNoDocsQueryNode
operator|.
name|class
argument_list|,
operator|new
name|MatchNoDocsQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|PrefixWildcardQueryNode
operator|.
name|class
argument_list|,
operator|new
name|PrefixWildcardQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|RangeQueryNode
operator|.
name|class
argument_list|,
operator|new
name|RangeQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|SlopQueryNode
operator|.
name|class
argument_list|,
operator|new
name|SlopQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|OriginalBooleanQueryNode
operator|.
name|class
argument_list|,
operator|new
name|OriginalBooleanQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|MultiPhraseQueryNode
operator|.
name|class
argument_list|,
operator|new
name|MultiPhraseQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|setBuilder
argument_list|(
name|MatchAllDocsQueryNode
operator|.
name|class
argument_list|,
operator|new
name|MatchAllDocsQueryNodeBuilder
argument_list|()
argument_list|)
expr_stmt|;
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
return|return
operator|(
name|Query
operator|)
name|super
operator|.
name|build
argument_list|(
name|queryNode
argument_list|)
return|;
block|}
block|}
end_class
end_unit
