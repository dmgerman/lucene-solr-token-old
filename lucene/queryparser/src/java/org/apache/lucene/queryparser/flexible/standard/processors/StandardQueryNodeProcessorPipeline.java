begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|processors
operator|.
name|NoChildOptimizationQueryNodeProcessor
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
name|QueryNodeProcessorPipeline
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
name|RemoveDeletedQueryNodesProcessor
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
name|builders
operator|.
name|StandardQueryTreeBuilder
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
name|parser
operator|.
name|StandardSyntaxParser
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
comment|/**  * This pipeline has all the processors needed to process a query node tree,  * generated by {@link StandardSyntaxParser}, already assembled.<br>  *<br>  * The order they are assembled affects the results.<br>  *<br>  * This processor pipeline was designed to work with  * {@link StandardQueryConfigHandler}.<br>  *<br>  * The result query node tree can be used to build a {@link Query} object using  * {@link StandardQueryTreeBuilder}.  *   * @see StandardQueryTreeBuilder  * @see StandardQueryConfigHandler  * @see StandardSyntaxParser  */
end_comment
begin_class
DECL|class|StandardQueryNodeProcessorPipeline
specifier|public
class|class
name|StandardQueryNodeProcessorPipeline
extends|extends
name|QueryNodeProcessorPipeline
block|{
DECL|method|StandardQueryNodeProcessorPipeline
specifier|public
name|StandardQueryNodeProcessorPipeline
parameter_list|(
name|QueryConfigHandler
name|queryConfig
parameter_list|)
block|{
name|super
argument_list|(
name|queryConfig
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|WildcardQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|MultiFieldQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|FuzzyQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|MatchAllDocsQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|OpenRangeQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|LegacyNumericQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|LegacyNumericRangeQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|PointQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|PointRangeQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|LowercaseExpandedTermsQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|TermRangeQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|AllowLeadingWildcardProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|AnalyzerQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|PhraseSlopQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
comment|//add(new GroupQueryNodeProcessor());
name|add
argument_list|(
operator|new
name|BooleanQuery2ModifierNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|NoChildOptimizationQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|RemoveDeletedQueryNodesProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|RemoveEmptyNonLeafQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|BooleanSingleChildOptimizationQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|DefaultPhraseSlopQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|BoostQueryNodeProcessor
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|MultiTermRewriteMethodProcessor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
